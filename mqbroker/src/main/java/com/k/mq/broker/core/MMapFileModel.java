package com.k.mq.broker.core;

import com.k.mq.broker.model.CommitLogMessageModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * 内存映射文件模型
 * 提供基于MMap（内存映射文件）的文件读写操作
 * 通过将文件映射到内存，实现高效的文件IO操作
 * <p>
 * 注意：JDK8存在bug，使用FileChannel.map映射后文件无法删除
 * 本类参考RocketMQ实现，通过反射调用Cleaner释放内存映射
 *
 * @author yihang07
 */
public class MMapFileModel {
    /**
     * 文件对象
     */
    private File file;
    /**
     * 内存映射缓冲区
     */
    private MappedByteBuffer mappedByteBuffer;
    /**
     * 文件通道
     */
    private FileChannel fileChannel;

    /**
     * 将文件加载到内存映射区
     *
     * @param filePath    文件路径
     * @param startOffset 映射起始偏移量
     * @param mappedSize  映射大小（字节数）
     * @throws IOException           当文件操作失败时抛出
     * @throws FileNotFoundException 当文件不存在时抛出
     */
    public void loadFileInMMap(String filePath, int startOffset, int mappedSize) throws IOException {
        file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist:" + filePath);
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
    }

    /**
     * 从映射内存中读取内容
     *
     * @param readOffset 读取起始偏移量
     * @param size       读取字节数
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
     * @param commitLogMessageModel 要写入的commitLogMessageModel对象
     */
    public void writeContent(CommitLogMessageModel commitLogMessageModel) {
        this.writeContent(commitLogMessageModel, false);
    }

    /**
     * 向映射内存写入内容
     *
     * @param commitLogMessageModel 要写入的commitLogMessageModel对象
     * @param force                 是否强制刷盘到磁盘
     */
    public void writeContent(CommitLogMessageModel commitLogMessageModel, boolean force) {
        mappedByteBuffer.put(commitLogMessageModel.convertToBytes());
        if (force) {
            mappedByteBuffer.force();
        }
    }

    /**
     * 清理MappedByteBuffer占用的内存
     * 解决JDK8中MappedByteBuffer导致文件无法删除的问题
     * <p>
     * 实现原理：
     * 1. 通过 viewed() 方法获取真正的 DirectByteBuffer（可能被包装过）
     * 2. 通过反射调用 DirectByteBuffer 的 cleaner() 方法获取 sun.misc.Cleaner 对象
     * 3. 调用 Cleaner 的 clean() 方法释放内存映射
     * <p>
     * 等价于：viewed(mappedByteBuffer).cleaner().clean()
     */
    public void clean() {
        // 检查 MappedByteBuffer 是否有效
        if (mappedByteBuffer == null || !mappedByteBuffer.isDirect() || mappedByteBuffer.capacity() == 0) return;
        // 链式调用：获取 viewed buffer -> 获取 cleaner -> 执行 clean
        invoke(invoke(viewed(mappedByteBuffer), "cleaner"), "clean");
    }

    /**
     * 通过反射调用对象的方法
     * 使用特权操作确保在安全管理器下也能正常执行
     *
     * @param target     目标对象
     * @param methodName 方法名
     * @param args       方法参数类型
     * @return 方法调用的返回值
     * @throws IllegalStateException 如果反射调用失败
     */
    private Object invoke(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged(
                new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            // 获取方法对象
                            Method method = method(target, methodName, args);
                            // 设置可访问，绕过访问控制（允许访问private方法）
                            method.setAccessible(true);
                            // 调用方法
                            return method.invoke(target);
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
        );
    }

    /**
     * 获取目标对象的指定方法
     * 先尝试获取public方法（包括继承的），如果找不到再获取当前类声明的方法（包括private）
     *
     * @param target     目标对象
     * @param methodName 方法名
     * @param args       方法参数类型
     * @return Method对象
     * @throws NoSuchMethodException 如果方法不存在
     */
    private Method method(Object target, String methodName, Class<?>[] args) throws NoSuchMethodException {
        try {
            // 先尝试获取public方法（包括从父类继承的）
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            // 如果找不到，获取当前类声明的方法（包括private、protected）
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    /**
     * 递归获取真正的DirectByteBuffer
     * 因为MappedByteBuffer可能被包装过（如通过slice()或duplicate()），
     * 需要递归调用viewedBuffer()或attachment()方法找到最底层的DirectByteBuffer
     *
     * @param buffer ByteBuffer对象
     * @return 真正的DirectByteBuffer
     */
    private ByteBuffer viewed(ByteBuffer buffer) {
        // 默认使用viewedBuffer方法名（JDK 8）
        String methodName = "viewedBuffer";

        // 检查是否有attachment方法（JDK 9+）
        Method[] methods = buffer.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals("attachment")) {
                methodName = "attachment";
                break;
            }
        }
        // 调用viewedBuffer()或attachment()方法获取底层buffer
        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);

        // 如果返回null，说明当前buffer就是最底层的
        if (viewedBuffer == null) return buffer;

        // 否则继续递归查找
        return viewed(viewedBuffer);
    }
}
