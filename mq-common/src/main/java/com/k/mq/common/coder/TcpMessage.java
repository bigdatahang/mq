package com.k.mq.common.coder;

public class TcpMessage {
    private short magic;
    private int code;
    private int len;
    private byte[] body;

    public TcpMessage() {
    }

    public TcpMessage(short magic, int code, int len, byte[] body) {
        this.magic = magic;
        this.code = code;
        this.len = len;
        this.body = body;
    }

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
