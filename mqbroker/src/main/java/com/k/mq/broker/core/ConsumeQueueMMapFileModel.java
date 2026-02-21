package com.k.mq.broker.core;

import com.k.mq.broker.util.PutMessageLock;
import com.k.mq.broker.util.UnfairReentrantLock;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

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

    /**
     * consumeQueue文件名称
     */
    private String consumeQueueFileName;

    public void loadFileInMMap(String topic, Integer queueId, String consumeQueueFileName, int startOffset, int mappedSize) throws IOException {
        this.topic = topic;
        this.queueId = queueId;
        this.consumeQueueFileName = consumeQueueFileName;
    }
}
