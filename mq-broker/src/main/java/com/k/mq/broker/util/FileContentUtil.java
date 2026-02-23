package com.k.mq.broker.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件内容读取工具类
 * 提供读取文件内容的工具方法
 *
 * @author yihang07
 */
public class FileContentUtil {
    /**
     * 从文件中读取全部内容
     *
     * @param path 文件路径
     * @return 文件内容字符串
     * @throws RuntimeException 当文件读取失败时抛出
     */
    public static String readFromFile(String path) {
        try (BufferedReader in = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            while (in.ready()) {
                sb.append(in.readLine());
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 覆盖写入内容到文件
     * 如果文件已存在，将清空原有内容后写入新内容
     * 
     * @param path 文件路径
     * @param content 要写入的内容
     * @throws RuntimeException 当文件写入失败时抛出
     */
    public static void overwriteToFile(String path, String content) {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
