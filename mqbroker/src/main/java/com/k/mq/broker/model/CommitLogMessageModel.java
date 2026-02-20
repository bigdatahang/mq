package com.k.mq.broker.model;

import com.k.mq.broker.util.ByteConvertUtil;

/**
 * CommitLog消息模型
 * 表示一条写入CommitLog的消息
 * 消息格式：4字节大小 + 消息内容
 * 
 * @author yihang07
 */
public class CommitLogMessageModel {
    /** 消息大小（字节数） */
    private int size;
    /** 消息内容 */
    private byte[] content;

    /**
     * 获取消息大小
     * 
     * @return 消息大小（字节数）
     */
    public int getSize() {
        return size;
    }

    /**
     * 设置消息大小
     * 
     * @param size 消息大小（字节数）
     */
    public void setSize(int size) {
        this.size = size;
    }

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
     * 格式：[4字节大小][消息内容]
     * 
     * @return 完整的消息字节数组
     */
    public byte[] convertToBytes() {
        // 将size转换为4字节数组
        byte[] bytes = ByteConvertUtil.intToBytes(this.size);
        // 合并size字节和content字节
        byte[] mergeBytes = new byte[bytes.length + this.content.length];
        int j = 0;
        // 复制size字节
        for (int i = 0; i < bytes.length; i++, j++) {
            mergeBytes[j] = bytes[i];
        }
        // 复制content字节
        for (int i = 0; i < content.length; i++, j++) {
            mergeBytes[j] = content[i];
        }
        return mergeBytes;
    }
}
