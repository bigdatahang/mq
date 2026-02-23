package com.k.mq.broker.core;

import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.MQTopicModel;
import com.k.mq.broker.model.QueueModel;
import com.k.mq.broker.util.LogFileNameUtil;
import com.k.mq.broker.util.PutMessageLock;
import com.k.mq.broker.util.UnfairReentrantLock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ConsumeQueue内存映射文件模型
 * 负责ConsumeQueue文件的内存映射和消息索引的读写操作
 * ConsumeQueue存储消息在CommitLog中的位置信息，每条记录固定12字节
 *
 * @author yihang07
 */
public class ConsumeQueueMMapFileModel {
    public static final int CONSUME_QUEUE_MESSAGE_SIZE = 12;
    /**
     * 文件对象
     */
    private File file;

    /**
     * 内存映射缓冲区
     */
    private MappedByteBuffer mappedByteBuffer;

    /**
     * 文件内容读取映射缓冲区
     */
    private ByteBuffer readBuffer;

    /**
     * 文件通道
     */
    private FileChannel fileChannel;

    /**
     * 主题名称
     */
    private String topic;

    /**
     * 写入消息时使用的锁，保证并发写入的线程安全性
     */
    private PutMessageLock putMessageLock;

    /**
     * 队列ID
     */
    private Integer queueId;

    /**
     * 将ConsumeQueue文件加载到内存映射区
     *
     * @param topic       主题名称
     * @param queueId     队列ID
     * @param startOffset 映射起始偏移量
     * @param mappedSize  映射大小（字节数）
     * @throws IOException 当文件操作失败时抛出
     */
    public void loadFileInMMap(String topic, Integer queueId, int startOffset, int mappedSize) throws IOException {
        this.topic = topic;
        this.queueId = queueId;
        this.putMessageLock = new UnfairReentrantLock();
        String filePath = getLatestConsumeQueueFile();
        this.doMMap(filePath, startOffset, mappedSize);
    }

    /**
     * 执行内存映射操作
     *
     * @param filePath    文件路径
     * @param startOffset 映射起始偏移量
     * @param mappedSize  映射大小（字节数）
     * @throws IOException           当文件操作失败时抛出
     * @throws FileNotFoundException 当文件不存在时抛出
     */
    private void doMMap(String filePath, int startOffset, int mappedSize) throws IOException {
        file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + filePath);
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
        readBuffer = mappedByteBuffer.slice();
    }

    /**
     * 写入消息索引内容（不强制刷盘）
     *
     * @param content 消息索引内容（12字节）
     * @throws IOException 当文件操作失败时抛出
     */
    public void writeContent(byte[] content) throws IOException {
        this.writeContent(content, false);
    }

    /**
     * 写入消息索引内容到ConsumeQueue
     *
     * @param content 消息索引内容（12字节）
     * @param force   是否强制刷盘
     * @throws IOException 当文件操作失败时抛出
     */
    public void writeContent(byte[] content, boolean force) throws IOException {
        MQTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topic);
        if (mqTopicModel == null) {
            throw new IllegalArgumentException("topic does not exist, topic is: " + topic);
        }
        QueueModel queueModel = mqTopicModel.getQueueList().get(queueId);
        if (queueModel == null) {
            throw new IllegalArgumentException("queueModel does not exist, queueId is: " + queueId);
        }

        try {
            putMessageLock.lock();
            // 检查空间是否足够，不足则创建新文件
            this.checkConsumeQueueHasEnableSpace(content.length, queueModel);

            // 设置写入位置并写入内容
            int currentOffset = queueModel.getLatestOffset().get();
            mappedByteBuffer.position(currentOffset);
            mappedByteBuffer.put(content);

            // 更新偏移量
            queueModel.getLatestOffset().addAndGet(content.length);

            // 强制刷盘
            if (force) {
                mappedByteBuffer.force();
            }
        } finally {
            putMessageLock.unlock();
        }
    }

    public byte[] readContent(int pos) {
        ByteBuffer byteBuffer = readBuffer.slice();
        byteBuffer.position(pos);
        byte[] content = new byte[CONSUME_QUEUE_MESSAGE_SIZE];
        byteBuffer.get(content);
        return content;
    }

    /**
     * 检查ConsumeQueue是否有足够的可用空间
     * 如果空间不足，自动创建新的ConsumeQueue文件并重新映射
     *
     * @param messageSize 要写入的消息索引大小（字节数）
     * @param queueModel  队列配置模型
     * @throws IOException 当创建新文件或映射失败时抛出
     */
    private void checkConsumeQueueHasEnableSpace(int messageSize, QueueModel queueModel) throws IOException {
        // 获取业务层面的剩余空间
        int space = queueModel.diff();

        // 检查MappedByteBuffer的实际剩余空间
        int currentOffset = queueModel.getLatestOffset().get();
        int bufferRemaining = mappedByteBuffer.capacity() - currentOffset;

        // 使用实际剩余空间和业务剩余空间中的较小值
        int actualSpace = Math.min(space, bufferRemaining);

        // 如果消息大小超过实际可用空间，需要创建新文件
        if (messageSize > actualSpace) {
            ConsumeQueueFilePath newConsumeQueueFile = createNewConsumeQueueFile(queueModel.getFileName());
            String newFilePath = newConsumeQueueFile.getFilePath();

            // 更新queueModel的状态
            queueModel.setFileName(newConsumeQueueFile.getFileName());
            queueModel.setLatestOffset(new AtomicInteger(0));
            queueModel.setLastOffset(0);

            // 重新映射新文件
            doMMap(newFilePath, 0, queueModel.getOffsetLimit());
        }
    }

    /**
     * 获取Topic的最新ConsumeQueue文件路径
     * 根据当前offset和offsetLimit判断是使用现有文件还是创建新文件
     *
     * @return ConsumeQueue文件的完整路径
     * @throws IllegalArgumentException 如果Topic或QueueId不存在
     * @throws IllegalStateException    如果offset超过offsetLimit
     */
    private String getLatestConsumeQueueFile() {
        // 从缓存中获取Topic配置
        MQTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topic);
        if (mqTopicModel == null) {
            throw new IllegalArgumentException("topic does not exist, topic is: " + topic);
        }

        List<QueueModel> queueList = mqTopicModel.getQueueList();
        if (queueList == null || queueId >= queueList.size()) {
            throw new IllegalArgumentException("queueId does not exist, queueId is: " + queueId);
        }

        QueueModel queueModel = queueList.get(queueId);
        int diff = queueModel.diff();

        String filePath;
        if (diff == 0) {
            // offset已达到上限，需要创建新的ConsumeQueue文件
            filePath = this.createNewConsumeQueueFile(queueModel.getFileName()).getFilePath();
        } else if (diff > 0) {
            // offset未达到上限，使用当前ConsumeQueue文件
            filePath = LogFileNameUtil.buildConsumeQueueFileName(topic, queueId, queueModel.getFileName());
        } else {
            // offset超过了offsetLimit，这是异常情况
            throw new IllegalStateException(
                    String.format("Invalid ConsumeQueue state for topic '%s', queueId '%d': latestOffset=%d, offsetLimit=%d",
                            topic, queueId, queueModel.getLatestOffset().get(), queueModel.getOffsetLimit())
            );
        }
        return filePath;
    }

    /**
     * 创建新的ConsumeQueue文件
     * 生成新的文件名（递增），创建物理文件，并返回完整路径
     *
     * @param fileName 当前文件名
     * @return 新创建的ConsumeQueue文件的完整路径
     * @throws RuntimeException 当文件创建失败时抛出
     */
    private ConsumeQueueFilePath createNewConsumeQueueFile(String fileName) {
        // 生成新的文件名（在原文件名基础上递增）
        String newFileName = LogFileNameUtil.incrConsumeQueueName(fileName);

        // 构建完整的文件路径
        String newFilePath = LogFileNameUtil.buildConsumeQueueFileName(topic, queueId, newFileName);

        // 创建物理文件
        try {
            File file = new File(newFilePath);
            // 确保父目录存在
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            // 创建文件
            if (file.createNewFile()) {
                System.out.println("创建新的ConsumeQueue文件: " + newFilePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create ConsumeQueue file: " + newFilePath, e);
        }
        return new ConsumeQueueFilePath(newFileName, newFilePath);
    }

    /**
     * 获取主题名称
     *
     * @return 主题名称
     */
    public String getTopic() {
        return topic;
    }

    /**
     * 获取队列ID
     *
     * @return 队列ID
     */
    public Integer getQueueId() {
        return queueId;
    }


    class ConsumeQueueFilePath {
        /**
         * 文件名（如"00000001"）
         */
        private String fileName;
        /**
         * 文件完整路径
         */
        private String filePath;

        /**
         * 构造函数
         *
         * @param fileName 文件名
         * @param filePath 文件完整路径
         */
        public ConsumeQueueFilePath(String fileName, String filePath) {
            this.fileName = fileName;
            this.filePath = filePath;
        }

        /**
         * 获取文件名
         *
         * @return 文件名
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * 设置文件名
         *
         * @param fileName 文件名
         */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        /**
         * 获取文件完整路径
         *
         * @return 文件完整路径
         */
        public String getFilePath() {
            return filePath;
        }

        /**
         * 设置文件完整路径
         *
         * @param filePath 文件完整路径
         */
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
