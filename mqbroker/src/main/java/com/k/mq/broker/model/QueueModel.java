package com.k.mq.broker.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 队列模型
 * 表示一个消息队列的配置和状态信息
 *
 * @author yihang07
 */
public class QueueModel {
    /** 队列ID */
    private Integer id;
    
    /** ConsumeQueue文件名 */
    private String fileName;
    
    /** 偏移量上限（字节数） */
    private Integer offsetLimit;
    
    /** 
     * 当前最新的偏移量（运行时动态更新）
     * 随着消息的写入而不断增加，使用AtomicInteger保证线程安全
     */
    private AtomicInteger latestOffset;
    
    /** 
     * 上次持久化的偏移量（从配置文件加载）
     * 用于启动时确定内存映射的起始位置
     * 在系统启动后，会被复制到latestOffset作为起始值
     */
    private Integer lastOffset;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getOffsetLimit() {
        return offsetLimit;
    }

    public void setOffsetLimit(Integer offsetLimit) {
        this.offsetLimit = offsetLimit;
    }

    public AtomicInteger getLatestOffset() {
        return latestOffset;
    }

    public void setLatestOffset(AtomicInteger latestOffset) {
        this.latestOffset = latestOffset;
    }

    public Integer getLastOffset() {
        return lastOffset;
    }

    public void setLastOffset(Integer lastOffset) {
        this.lastOffset = lastOffset;
    }

    public int diff() {
        return offsetLimit - latestOffset.get();
    }

    @Override
    public String toString() {
        return "QueueModel{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", offsetLimit=" + offsetLimit +
                ", latestOffset=" + latestOffset +
                ", lastOffset=" + lastOffset +
                '}';
    }
}
