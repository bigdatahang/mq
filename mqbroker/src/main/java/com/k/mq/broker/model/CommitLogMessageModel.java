package com.k.mq.broker.model;

import com.k.mq.broker.util.ByteConvertUtil;

public class CommitLogMessageModel {
    private int size;
    private byte[] content;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] convertToBytes() {
        byte[] bytes = ByteConvertUtil.intToBytes(this.size);
        byte[] mergeBytes = new byte[bytes.length + this.content.length];
        int j = 0;
        for (int i = 0; i < bytes.length; i++, j++) {
            mergeBytes[j] = bytes[i];
        }
        for (int i = 0; i < content.length; i++, j++) {
            mergeBytes[j] = content[i];
        }
        return mergeBytes;
    }
}
