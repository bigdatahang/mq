package com.k.mq.broker.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * CommitLog模型
 * 表示CommitLog文件的配置和状态信息
 * 
 * @author yihang07
 */
public class CommitLogModel {
    /** CommitLog文件名 */
    private String fileName;
    /** 偏移量上限（字节数） */
    private Long offsetLimit;
    /** 当前偏移量（原子操作，支持并发） */
    private AtomicLong offset;

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
     * 获取偏移量上限
     * 
     * @return 偏移量上限
     */
    public Long getOffsetLimit() {
        return offsetLimit;
    }

    /**
     * 设置偏移量上限
     * 
     * @param offsetLimit 偏移量上限
     */
    public void setOffsetLimit(Long offsetLimit) {
        this.offsetLimit = offsetLimit;
    }

    /**
     * 获取当前偏移量（原子类型）
     * 
     * @return 当前偏移量的AtomicLong对象
     */
    public AtomicLong getOffset() {
        return offset;
    }

    /**
     * 设置当前偏移量
     * 
     * @param offset 当前偏移量的AtomicLong对象
     */
    public void setOffset(AtomicLong offset) {
        this.offset = offset;
    }

    /**
     * 计算剩余可用空间
     * 
     * @return 偏移量上限与当前偏移量的差值（剩余空间）
     */
    public Long diff() {
        return this.offsetLimit - this.offset.get();
    }

    @Override
    public String toString() {
        return "CommitLogModel{" +
                "fileName='" + fileName + '\'' +
                ", offsetLimit=" + offsetLimit +
                ", offset=" + offset +
                '}';
    }
}
