package com.k.mq.broker.model;

/**
 * 队列模型
 * 表示一个消息队列的配置和状态信息
 *
 * @author yihang07
 */
public class QueueModel {
    private Integer id;
    private String fileName;
    private Integer offsetLimit;
    private Integer latestOffset;
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

    public Integer getLatestOffset() {
        return latestOffset;
    }

    public void setLatestOffset(Integer latestOffset) {
        this.latestOffset = latestOffset;
    }

    public Integer getLastOffset() {
        return lastOffset;
    }

    public void setLastOffset(Integer lastOffset) {
        this.lastOffset = lastOffset;
    }

    public int diff() {
        return offsetLimit - latestOffset;
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
