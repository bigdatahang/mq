package com.k.mq.broker.model;

/**
 * CommitLog消息模型
 * 表示一条写入CommitLog的消息
 * 消息格式：4字节大小 + 消息内容
 *
 * @author yihang07
 */
public class CommitLogMessageModel {
    /**
     * 消息内容
     */
    private byte[] content;

    /**
     * 获取消息内容
     *
     * @return 消息内容字节数组
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * 设置消息内容
     *
     * @param content 消息内容字节数组
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * 将消息转换为字节数组
     *
     * @return 完整的消息字节数组
     */
    public byte[] convertToBytes() {
        return content;
    }
}
