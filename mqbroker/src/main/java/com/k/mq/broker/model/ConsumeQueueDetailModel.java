package com.k.mq.broker.model;

import com.k.mq.broker.util.ByteConvertUtil;

/**
 * ConsumeQueue详细信息模型
 * 存储消息在CommitLog中的位置信息，用于消息索引
 * 
 * <p>ConsumeQueue存储格式（每条记录12字节）：
 * <ul>
 *   <li>commitLogFileName (4字节): CommitLog文件名</li>
 *   <li>msgIndex (4字节): 消息在CommitLog中的物理偏移量</li>
 *   <li>msgLength (4字节): 消息长度</li>
 * </ul>
 * 
 * @author yihang07
 */
public class ConsumeQueueDetailModel {
    /** ConsumeQueue单条记录的固定大小（字节）：4 + 4 + 4 = 12 */
    public static final int CONSUME_QUEUE_DETAIL_SIZE = 12;
    
    /** CommitLog文件名（数字形式，如 00000001 存储为 1） */
    private int commitLogFileName;
    
    /** 消息在CommitLog文件中的物理偏移量（索引位置） */
    private int msgIndex;
    
    /** 消息长度（字节数） */
    private int msgLength;

    /**
     * 获取CommitLog文件名
     * 
     * @return CommitLog文件名的数字表示
     */
    public int getCommitLogFileName() {
        return commitLogFileName;
    }

    /**
     * 设置CommitLog文件名
     * 
     * @param commitLogFileName CommitLog文件名的数字表示
     */
    public void setCommitLogFileName(int commitLogFileName) {
        this.commitLogFileName = commitLogFileName;
    }

    /**
     * 获取消息在CommitLog中的索引位置
     * 
     * @return 消息的物理偏移量
     */
    public int getMsgIndex() {
        return msgIndex;
    }

    /**
     * 设置消息在CommitLog中的索引位置
     * 
     * @param msgIndex 消息的物理偏移量
     */
    public void setMsgIndex(int msgIndex) {
        this.msgIndex = msgIndex;
    }

    /**
     * 获取消息长度
     * 
     * @return 消息长度（字节数）
     */
    public int getMsgLength() {
        return msgLength;
    }

    /**
     * 设置消息长度
     * 
     * @param msgLength 消息长度（字节数）
     */
    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
    }

    /**
     * 将ConsumeQueue详细信息转换为字节数组
     * 
     * <p>字节数组格式（12字节）：
     * <pre>
     * [0-3]   commitLogFileName (4字节)
     * [4-7]   msgIndex (4字节)
     * [8-11]  msgLength (4字节)
     * </pre>
     * 
     * @return 12字节的字节数组
     */
    public byte[] convertToBytes() {
        byte[] commitLogFileNameBytes = ByteConvertUtil.intToBytes(commitLogFileName);
        byte[] msgIndexBytes = ByteConvertUtil.intToBytes(msgIndex);
        byte[] msgLengthBytes = ByteConvertUtil.intToBytes(msgLength);
        
        // 优化：使用固定大小常量，使用System.arraycopy提升性能
        byte[] result = new byte[CONSUME_QUEUE_DETAIL_SIZE];
        
        System.arraycopy(commitLogFileNameBytes, 0, result, 0, 4);
        System.arraycopy(msgIndexBytes, 0, result, 4, 4);
        System.arraycopy(msgLengthBytes, 0, result, 8, 4);
        
        return result;
    }

    @Override
    public String toString() {
        return "ConsumeQueueDetailModel{" +
                "commitLogFileName=" + commitLogFileName +
                ", msgIndex=" + msgIndex +
                ", msgLength=" + msgLength +
                '}';
    }
}
