package com.k.mq.common.coder;

import java.util.Arrays;

import static com.k.mq.common.constants.BrokerConstants.DEFAULT_MAGIC_NUM;

public class TcpMessage {
    private short magic;
    private int code;
    private int len;
    private byte[] body;

    public TcpMessage() {
    }

    public TcpMessage(int code, byte[] body) {
        this.code = code;
        this.body = body;
        this.magic = DEFAULT_MAGIC_NUM;
        this.len = body.length;
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

    @Override
    public String toString() {
        return "TcpMessage{" +
                "magic=" + magic +
                ", code=" + code +
                ", len=" + len +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
