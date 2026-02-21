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
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumeQueueMMapFileModel {
    /**
     * 文件对象
     */
    private File file;
    /**
     * 内存映射缓冲区
     */
    private MappedByteBuffer mappedByteBuffer;
    /**
     * 文件通道
     */
    private FileChannel fileChannel;

    /**
     * 主题名称
     * 用于标识当前MMapFileModel所属的Topic
     */
    private String topic;

    /**
     * 写入消息时使用的锁
     * 保证并发写入的线程安全性
     */
    private PutMessageLock putMessageLock;

    /**
     * 队列Id
     */
    private Integer queueId;

    public void loadFileInMMap(String topic, Integer queueId, int startOffset, int mappedSize) throws IOException {
        this.topic = topic;
        this.queueId = queueId;
        String filePath = getLatestConsumeQueueFile();
        this.doMMap(filePath, startOffset, mappedSize);
        putMessageLock = new UnfairReentrantLock();
    }

    private void doMMap(String filePath, int startOffset, int mappedSize) throws IOException {
        file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist:" + filePath);
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
    }

    public void writeContent(byte[] content) throws IOException {
        this.writeContent(content, false);
    }

    public void writeContent(byte[] content, boolean force) throws IOException {
        Map<String, MQTopicModel> mqTopicModelMap = CommonCache.getMqTopicModelMap();
        MQTopicModel mqTopicModel = mqTopicModelMap.get(topic);
        if (mqTopicModel == null) {
            throw new IllegalArgumentException("topic does not exist, topic is: " + topic);
        }
        QueueModel queueModel = mqTopicModel.getQueueList().get(queueId);
        try {
            putMessageLock.lock();
            this.checkConsumeQueueHasEnableSpace(content.length, queueModel);
            mappedByteBuffer.position(queueModel.getLatestOffset().get());
            mappedByteBuffer.put(content);
            // 强制刷盘
            if (force) {
                mappedByteBuffer.force();
            }
        } finally {
            putMessageLock.unlock();
        }
    }

    private void checkConsumeQueueHasEnableSpace(int messageSize, QueueModel queueModel) throws IOException {
        // 获取业务层面的剩余空间
        long space = queueModel.diff();

        // 【修复】检查MappedByteBuffer的实际剩余空间
        // 当前offset位置之后的实际可用空间
        int currentOffset = queueModel.getLatestOffset().get();
        int bufferRemaining = mappedByteBuffer.capacity() - currentOffset;

        // 使用实际剩余空间和业务剩余空间中的较小值
        long actualSpace = Math.min(space, bufferRemaining);

        // 如果消息大小超过实际可用空间，需要创建新文件
        if (messageSize > actualSpace) {
            String newFilePath = createNewConsumeQueueFile(queueModel.getFileName());
            queueModel.setId(queueId);
            queueModel.setFileName(newFilePath);
            queueModel.setLatestOffset(new AtomicInteger(0));
            queueModel.setLastOffset(0);
            queueModel.setOffsetLimit(queueModel.getOffsetLimit());
            doMMap(newFilePath, 0, queueModel.getOffsetLimit());
        }
    }

    private String getLatestConsumeQueueFile() {
        // 从缓存中获取Topic配置
        MQTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topic);
        if (mqTopicModel == null) {
            throw new IllegalArgumentException("topic does not exist, topic is: " + topic);
        }
        List<QueueModel> queueList = mqTopicModel.getQueueList();
        QueueModel queueModel = queueList.get(queueId);
        if (queueModel == null) {
            throw new IllegalArgumentException("queueId does not exist, queueId is: " + queueId);
        }
        long diff = queueModel.diff();

        String filePath;
        if (diff == 0) {
            // offset已达到上限，需要创建新的CommitLog文件
            filePath = this.createNewConsumeQueueFile(queueModel.getFileName());
        } else if (diff > 0) {
            // offset未达到上限，使用当前CommitLog文件
            filePath = LogFileNameUtil.buildConsumeQueueFileName(topic, queueId, queueModel.getFileName());
        } else {
            // offset超过了offsetLimit，这是异常情况
            throw new IllegalStateException(
                    String.format("Invalid consumeQueue state for topic '%s': latestOffset=%d, offsetLimit=%d",
                            topic, queueModel.getLatestOffset(), queueModel.getOffsetLimit())
            );
        }
        return filePath;
    }

    private String createNewConsumeQueueFile(String fileName) {
        String newFileName = LogFileNameUtil.incrConsumeQueueName(fileName);
        String newFilePath = LogFileNameUtil.buildConsumeQueueFileName(topic, queueId, newFileName);
        // 创建物理文件
        try {
            File file = new File(newFilePath);  // 使用完整路径而不是只有文件名
            // 确保父目录存在
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            // 创建文件
            file.createNewFile();
            System.out.println("创建新的consumeQueue文件");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create consumeQueue file: " + newFilePath, e);
        }
        return newFilePath;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getQueueId() {
        return queueId;
    }
}
