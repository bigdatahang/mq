package com.k.mq.broker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 内存映射文件工具类
 * 提供基于MMap（内存映射文件）的文件读写操作
 * 通过将文件映射到内存，实现高效的文件IO操作
 *
 * @author yihang07
 */
public class MMapUtil {
    private File file;
    private MappedByteBuffer mappedByteBuffer;
    private FileChannel fileChannel;


    /**
     * 将文件加载到内存映射区
     *
     * @param filePath 文件路径
     * @param startOffset 映射起始偏移量
     * @param mappedSize 映射大小（字节数）
     * @throws IOException 当文件操作失败时抛出
     * @throws FileNotFoundException 当文件不存在时抛出
     */
    public void loadFileInMMap(String filePath, int startOffset, int mappedSize) throws IOException {
        file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("file path is :" + filePath + "not exists");
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
    }

    /**
     * 从映射内存中读取内容
     *
     * @param readOffset 读取起始偏移量
     * @param size 读取字节数
     * @return 读取的字节数组
     */
    public byte[] readContent(int readOffset, int size) {
        mappedByteBuffer.position(readOffset);
        byte[] content = new byte[size];
        int j = 0;
        for (int i = 0; i < size; i++) {
            content[j++] = mappedByteBuffer.get(readOffset + i);
        }
        return content;
    }

    /**
     * 向映射内存写入内容（非强制刷盘）
     *
     * @param content 要写入的字节数组
     */
    public void writeContent(byte[] content) {
        this.writeContent(content, false);
    }

    /**
     * 向映射内存写入内容
     *
     * @param content 要写入的字节数组
     * @param force 是否强制刷盘到磁盘
     */
    public void writeContent(byte[] content, boolean force) {
        mappedByteBuffer.put(content);
        if (force) {
            mappedByteBuffer.force();
        }
    }
}
