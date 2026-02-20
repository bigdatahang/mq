package com.k.mq.broker.util;

import com.alibaba.fastjson2.JSON;
import com.k.mq.broker.model.MQTopicModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public class FileContentReaderUtil {
    public static String readFromFile(String path) {
        try (BufferedReader in = new BufferedReader(new FileReader(path))) {
            StringBuffer sb = new StringBuffer();
            while (in.ready()) {
                sb.append(in.readLine());
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String json = readFromFile("/Users/yihang07/code/mq/broker/config/mq-topic.json");
        List<MQTopicModel> mqTopicModels = JSON.parseArray(json, MQTopicModel.class);
        System.out.println(mqTopicModels);
    }
}
