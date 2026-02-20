package com.k.mq.broker.model;

/**
 * 队列模型
 * 表示一个消息队列的配置和状态信息
 * 
 * @author yihang07
 */
public class QueueModel {
    /** 队列ID */
    private Integer id;
    /** 最小偏移量 */
    private Long minOffset;
    /** 最大偏移量 */
    private Long maxOffset;
    /** 当前偏移量 */
    private Long currentOffset;

    /**
     * 获取队列ID
     * 
     * @return 队列ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置队列ID
     * 
     * @param id 队列ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取最小偏移量
     * 
     * @return 最小偏移量
     */
    public Long getMinOffset() {
        return minOffset;
    }

    /**
     * 设置最小偏移量
     * 
     * @param minOffset 最小偏移量
     */
    public void setMinOffset(Long minOffset) {
        this.minOffset = minOffset;
    }

    /**
     * 获取最大偏移量
     * 
     * @return 最大偏移量
     */
    public Long getMaxOffset() {
        return maxOffset;
    }

    /**
     * 设置最大偏移量
     * 
     * @param maxOffset 最大偏移量
     */
    public void setMaxOffset(Long maxOffset) {
        this.maxOffset = maxOffset;
    }

    /**
     * 获取当前偏移量
     * 
     * @return 当前偏移量
     */
    public Long getCurrentOffset() {
        return currentOffset;
    }

    /**
     * 设置当前偏移量
     * 
     * @param currentOffset 当前偏移量
     */
    public void setCurrentOffset(Long currentOffset) {
        this.currentOffset = currentOffset;
    }

    @Override
    public String toString() {
        return "QueueModel{" +
                "id=" + id +
                ", minOffset=" + minOffset +
                ", maxOffset=" + maxOffset +
                ", currentOffset=" + currentOffset +
                '}';
    }
}
