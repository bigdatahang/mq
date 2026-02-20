package com.k.mq.broker.util;

import com.alibaba.fastjson2.JSON;
import com.k.mq.broker.model.MQTopicModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

/**
 * 文件内容读取工具类
 * 提供读取文件内容的工具方法
 * 
 * @author yihang07
 */
public class FileContentReaderUtil {
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
     * 测试入口
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        String json = readFromFile("/Users/yihang07/code/mq/broker/config/mq-topic.json");
        List<MQTopicModel> mqTopicModels = JSON.parseArray(json, MQTopicModel.class);
        System.out.println(mqTopicModels);
    }
}
