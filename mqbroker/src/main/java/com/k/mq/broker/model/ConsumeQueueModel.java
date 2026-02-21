package com.k.mq.broker.model;

public class ConsumeQueueModel {
    private String commitLogFileName;
    private Long msgIndex;
    private int msgLength;

    public String getCommitLogFileName() {
        return commitLogFileName;
    }

    public void setCommitLogFileName(String commitLogFileName) {
        this.commitLogFileName = commitLogFileName;
    }

    public Long getMsgIndex() {
        return msgIndex;
    }

    public void setMsgIndex(Long msgIndex) {
        this.msgIndex = msgIndex;
    }

    public int getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
    }
}
