package com.k.mq.broker.core;

import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.*;
import com.k.mq.broker.util.LogFileNameUtil;
import com.k.mq.broker.util.PutMessageLock;
import com.k.mq.broker.util.UnfairReentrantLock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.k.mq.broker.constants.BrokerConstants.COMMIT_LOG_DEFAULT_MAPPED_SIZE;

/**
 * 内存映射文件模型
 * 提供基于MMap（内存映射文件）的文件读写操作
 * 通过将文件映射到内存，实现高效的文件IO操作
 * <p>
 * 注意：JDK8存在bug，使用FileChannel.map映射后文件无法删除
 * 本类参考RocketMQ实现，通过反射调用Cleaner释放内存映射
 *
 * @author yihang07
 */
public class MMapFileModel {
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
    private PutMessageLock lock = new UnfairReentrantLock();

    /**
     * 将文件加载到内存映射区
     *
     * @param topic       主题名称
     * @param startOffset 映射起始偏移量
     * @param mappedSize  映射大小（字节数）
     * @throws IOException           当文件操作失败时抛出
     * @throws FileNotFoundException 当文件不存在时抛出
     */
    public void loadFileInMMap(String topic, int startOffset, int mappedSize) throws IOException {
        String filePath = getLatestCommitLogFile(topic);
        this.topic = topic;
        this.doMMap(filePath, startOffset, mappedSize);
    }

    /**
     * 执行内存映射操作
     * 将指定文件映射到内存中
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
            throw new FileNotFoundException("File does not exist:" + filePath);
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
    }

    /**
     * 获取Topic的最新CommitLog文件路径
     * 根据当前offset和offsetLimit判断是使用现有文件还是创建新文件
     *
     * @param topic 主题名称
     * @return CommitLog文件的完整路径
     * @throws IllegalArgumentException 如果Topic不存在
     * @throws IllegalStateException    如果offset超过offsetLimit
     */
    private String getLatestCommitLogFile(String topic) {
        // 从缓存中获取Topic配置
        MQTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topic);
        if (mqTopicModel == null) {
            throw new IllegalArgumentException("topic does not exist, topic is: " + topic);
        }

        // 获取CommitLog配置信息
        CommitLogModel commitLogModel = mqTopicModel.getCommitLogModel();
        long diff = commitLogModel.diff();

        String filePath;
        if (diff == 0) {
            // offset已达到上限，需要创建新的CommitLog文件
            filePath = createNewCommitLogFile(topic, commitLogModel).getFilePath();
        } else if (diff > 0) {
            // offset未达到上限，使用当前CommitLog文件
            filePath = LogFileNameUtil.buildCommitLogFileName(topic, commitLogModel.getFileName());
        } else {
            // offset超过了offsetLimit，这是异常情况
            throw new IllegalStateException(
                    String.format("Invalid CommitLog state for topic '%s': offset=%d, offsetLimit=%d",
                            topic, commitLogModel.getOffset(), commitLogModel.getOffsetLimit())
            );
        }
        return filePath;
    }

    /**
     * CommitLog文件路径封装类
     * 用于同时返回文件名和完整路径
     *
     * @author yihang07
     */
    class CommitLogFilePath {
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
        public CommitLogFilePath(String fileName, String filePath) {
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

    /**
     * 创建新的CommitLog文件
     * 生成新的文件名（递增），创建物理文件，并返回完整路径
     *
     * @param topic          主题名称
     * @param commitLogModel CommitLog配置模型
     * @return 新创建的CommitLog文件的完整路径
     * @throws RuntimeException 当文件创建失败时抛出
     */
    private CommitLogFilePath createNewCommitLogFile(String topic, CommitLogModel commitLogModel) {
        // 生成新的文件名（在原文件名基础上递增）
        String newFileName = LogFileNameUtil.incrCommitLogName(commitLogModel.getFileName());

        // 构建完整的文件路径
        String newFilePath = LogFileNameUtil.buildCommitLogFileName(topic, newFileName);

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
            System.out.println("创建新的commitLog文件");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create CommitLog file: " + newFilePath, e);
        }
        return new CommitLogFilePath(newFileName, newFilePath);
    }

    /**
     * 从映射内存中读取内容
     *
     * @param readOffset 读取起始偏移量
     * @param size       读取字节数
     * @return 读取的字节数组
     */
    public byte[] readContent(int readOffset, int size) {
        mappedByteBuffer.position(readOffset);
        byte[] content = new byte[size];
        int j = 0;
        for (int i = 0; i < size; i++) {
            content[j++] = mappedByteBuffer.get(readOffset + i);
        }
        return content;
    }

    /**
     * 向映射内存写入内容（非强制刷盘）
     *
     * @param commitLogMessageModel 要写入的commitLogMessageModel对象
     */
    public void writeContent(CommitLogMessageModel commitLogMessageModel) throws IOException {
        this.writeContent(commitLogMessageModel, false);
    }

    /**
     * 向映射内存写入内容
     *
     * @param commitLogMessageModel 要写入的commitLogMessageModel对象
     * @param force                 是否强制刷盘到磁盘
     */
    public void writeContent(CommitLogMessageModel commitLogMessageModel, boolean force) throws IOException {
        MQTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topic);
        if (mqTopicModel == null) {
            throw new IllegalArgumentException("MQTopicModel is null");
        }
        CommitLogModel commitLogModel = mqTopicModel.getCommitLogModel();
        if (commitLogModel == null) {
            throw new IllegalArgumentException("CommitLogModel is null");
        }

        try {
            lock.lock();
            // 【修复】先转换为字节数组，获取实际要写入的字节数
            byte[] writeContent = commitLogMessageModel.convertToBytes();

            // 使用实际字节数组的长度来检查空间
            checkCommitLogHasEnableSpace(writeContent.length, commitLogModel);

            // 设置position到当前offset
            int currentOffset = commitLogModel.getOffset().get();
            mappedByteBuffer.position((int) currentOffset);

            // 写入数据
            mappedByteBuffer.put(writeContent);
            this.dispatcher(writeContent, currentOffset);

            // 更新offset（使用实际写入的长度）
            commitLogModel.getOffset().addAndGet(writeContent.length);

            // 强制刷盘
            if (force) {
                mappedByteBuffer.force();
            }
        } finally {
            lock.unlock();
        }
    }

    private void dispatcher(byte[] writeContent, int msgIndex) throws IOException {
        MQTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topic);
        if (mqTopicModel == null) {
            throw new RuntimeException("MQTopicModel IS NULL");
        }
        // TODO queueId的计算逻辑
        int queueId = 0;
        ConsumeQueueDetailModel consumeQueueDetailModel = new ConsumeQueueDetailModel();
        consumeQueueDetailModel.setCommitLogFileName(Integer.parseInt(mqTopicModel.getCommitLogModel().getFileName()));
        consumeQueueDetailModel.setMsgIndex(msgIndex);
        consumeQueueDetailModel.setMsgLength(writeContent.length);
        byte[] content = consumeQueueDetailModel.convertToBytes();
        List<ConsumeQueueMMapFileModel> consumeQueueMMapFileModels = CommonCache.getConsumeQueueMMapFileModelManager().get(topic);
        ConsumeQueueMMapFileModel consumeQueueMMapFileModel = consumeQueueMMapFileModels.stream().filter(queueModel -> queueModel.getQueueId().equals(queueId)).findFirst().orElse(null);
        if (consumeQueueMMapFileModel != null) {
            consumeQueueMMapFileModel.writeContent(content);
        }
        QueueModel queueModel = mqTopicModel.getQueueList().get(queueId);
        queueModel.getLatestOffset().addAndGet(content.length);
    }

    /**
     * 检查CommitLog是否有足够的可用空间
     * 如果空间不足，自动创建新的CommitLog文件并重新映射
     *
     * @param messageSize    要写入的消息大小（字节数）
     * @param commitLogModel CommitLog配置模型
     * @throws IOException 当创建新文件或映射失败时抛出
     */
    private void checkCommitLogHasEnableSpace(int messageSize, CommitLogModel commitLogModel) throws IOException {
        // 获取业务层面的剩余空间
        long space = commitLogModel.diff();

        // 【修复】检查MappedByteBuffer的实际剩余空间
        // 当前offset位置之后的实际可用空间
        int currentOffset = commitLogModel.getOffset().get();
        int bufferRemaining = mappedByteBuffer.capacity() - currentOffset;

        // 使用实际剩余空间和业务剩余空间中的较小值
        long actualSpace = Math.min(space, bufferRemaining);

        // 如果消息大小超过实际可用空间，需要创建新文件
        if (messageSize > actualSpace) {
            CommitLogFilePath newCommitLogFile = createNewCommitLogFile(topic, commitLogModel);
            commitLogModel.setOffset(new AtomicInteger(0));
            commitLogModel.setOffsetLimit(Long.valueOf(COMMIT_LOG_DEFAULT_MAPPED_SIZE));
            commitLogModel.setFileName(newCommitLogFile.getFileName());

            doMMap(newCommitLogFile.getFilePath(), 0, COMMIT_LOG_DEFAULT_MAPPED_SIZE);
        }
    }

    /**
     * 清理MappedByteBuffer占用的内存
     * 解决JDK8中MappedByteBuffer导致文件无法删除的问题
     * <p>
     * 实现原理：
     * 1. 通过 viewed() 方法获取真正的 DirectByteBuffer（可能被包装过）
     * 2. 通过反射调用 DirectByteBuffer 的 cleaner() 方法获取 sun.misc.Cleaner 对象
     * 3. 调用 Cleaner 的 clean() 方法释放内存映射
     * <p>
     * 等价于：viewed(mappedByteBuffer).cleaner().clean()
     */
    public void clean() {
        // 检查 MappedByteBuffer 是否有效
        if (mappedByteBuffer == null || !mappedByteBuffer.isDirect() || mappedByteBuffer.capacity() == 0) return;
        // 链式调用：获取 viewed buffer -> 获取 cleaner -> 执行 clean
        invoke(invoke(viewed(mappedByteBuffer), "cleaner"), "clean");
    }

    /**
     * 通过反射调用对象的方法
     * 使用特权操作确保在安全管理器下也能正常执行
     *
     * @param target     目标对象
     * @param methodName 方法名
     * @param args       方法参数类型
     * @return 方法调用的返回值
     * @throws IllegalStateException 如果反射调用失败
     */
    private Object invoke(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged(
                new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            // 获取方法对象
                            Method method = method(target, methodName, args);
                            // 设置可访问，绕过访问控制（允许访问private方法）
                            method.setAccessible(true);
                            // 调用方法
                            return method.invoke(target);
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
        );
    }

    /**
     * 获取目标对象的指定方法
     * 先尝试获取public方法（包括继承的），如果找不到再获取当前类声明的方法（包括private）
     *
     * @param target     目标对象
     * @param methodName 方法名
     * @param args       方法参数类型
     * @return Method对象
     * @throws NoSuchMethodException 如果方法不存在
     */
    private Method method(Object target, String methodName, Class<?>[] args) throws NoSuchMethodException {
        try {
            // 先尝试获取public方法（包括从父类继承的）
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            // 如果找不到，获取当前类声明的方法（包括private、protected）
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    /**
     * 递归获取真正的DirectByteBuffer
     * 因为MappedByteBuffer可能被包装过（如通过slice()或duplicate()），
     * 需要递归调用viewedBuffer()或attachment()方法找到最底层的DirectByteBuffer
     *
     * @param buffer ByteBuffer对象
     * @return 真正的DirectByteBuffer
     */
    private ByteBuffer viewed(ByteBuffer buffer) {
        // 默认使用viewedBuffer方法名（JDK 8）
        String methodName = "viewedBuffer";

        // 检查是否有attachment方法（JDK 9+）
        Method[] methods = buffer.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals("attachment")) {
                methodName = "attachment";
                break;
            }
        }
        // 调用viewedBuffer()或attachment()方法获取底层buffer
        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);

        // 如果返回null，说明当前buffer就是最底层的
        if (viewedBuffer == null) return buffer;

        // 否则继续递归查找
        return viewed(viewedBuffer);
    }
}
