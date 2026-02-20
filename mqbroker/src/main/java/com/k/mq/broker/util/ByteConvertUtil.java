package com.k.mq.broker.util;

public class ByteConvertUtil {
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xff);
        src[2] = (byte) ((value >> 16) & 0xff);
        src[1] = (byte) ((value >> 8) & 0xff);
        src[0] = (byte) (value & 0xff);
        return src;
    }

    public static int bytesToInt(byte[] ary) {
        return (ary[3] & 0xff) << 24
                | (ary[2] & 0xff) << 16
                | (ary[1] & 0xff) << 8
                | (ary[0] & 0xff);
    }

    public static void main(String[] args) {
        int value = 100;
        byte[] src = intToBytes(value);
        System.out.println(src);
        System.out.println(bytesToInt(src));
    }
}
