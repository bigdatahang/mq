package com.k.mq.broker.util;

/**
 * 字节转换工具类
 * 提供基本数据类型与字节数组之间的相互转换
 * 使用小端序（Little-Endian）格式
 *
 * @author yihang07
 */
public class ByteConvertUtil {
    /**
     * 将整数转换为字节数组（小端序）
     * 整数的最低字节存储在数组索引0位置
     *
     * @param value 要转换的整数值
     * @return 4字节数组，按小端序存储
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xff);  // 最高字节
        src[2] = (byte) ((value >> 16) & 0xff);
        src[1] = (byte) ((value >> 8) & 0xff);
        src[0] = (byte) (value & 0xff);          // 最低字节
        return src;
    }

    /**
     * 将字节数组转换为整数（小端序）
     * 假定输入是4字节的小端序字节数组
     *
     * @param ary 4字节数组，小端序格式
     * @return 转换后的整数值
     */
    public static int bytesToInt(byte[] ary) {
        return (ary[3] & 0xff) << 24
                | (ary[2] & 0xff) << 16
                | (ary[1] & 0xff) << 8
                | (ary[0] & 0xff);
    }


    public static byte[] readInPos(byte[] src, int pos, int len) {
        byte[] result = new byte[len];
        for (int i = pos, j = 0; i < pos + len; i++) {
            result[j++] = src[i];
        }
        return result;
    }

    /**
     * 测试入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        int value = 100;
        byte[] src = intToBytes(value);
        System.out.println(src);
        System.out.println(bytesToInt(src));
    }
}
