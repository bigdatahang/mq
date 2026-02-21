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
    private PutMessageLock lock = new UnfairReentrantLock();

    /**
     * 队列Id
     */
    private Integer queueId;

    public void loadFileInMMap(String topic, Integer queueId, int startOffset, int mappedSize) throws IOException {
        this.topic = topic;
        this.queueId = queueId;
        String filePath = getLatestConsumeQueueFile();
        this.doMMap(filePath, startOffset, mappedSize);
    }

    private void doMMap(String filePath, int startOffset, int mappedSize) throws IOException {
        file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist:" + filePath);
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
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
            filePath = this.createNewCommitLogFile(queueModel.getFileName());
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

    private String createNewCommitLogFile(String fileName) {
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
}
