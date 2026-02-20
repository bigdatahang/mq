package com.k.mq.broker.config;

import com.alibaba.fastjson.JSON;
import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.MQTopicModel;
import com.k.mq.broker.util.FileContentReaderUtil;
import io.netty.util.internal.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MQ主题配置加载器
 * 从配置文件中加载Topic相关的配置信息
 *
 * @author yihang07
 */
public class MQTopicLoader {

    /**
     * 加载Topic配置
     * 从mq-topic.json文件中读取并解析所有Topic的配置信息
     */
    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String mqHome = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(mqHome)) {
            throw new IllegalArgumentException("MQ_HOME IS NULL");
        }
        String jsonFilePath = mqHome + "/broker/config/mq-topic.json";
        List<MQTopicModel> mqTopicModelList = JSON.parseArray(FileContentReaderUtil.readFromFile(jsonFilePath), MQTopicModel.class);
        Map<String, MQTopicModel> mqTopicModelMap = mqTopicModelList.stream().collect(Collectors.toMap(MQTopicModel::getTopic, item -> item));
        CommonCache.setMqTopicModelMap(mqTopicModelMap);
    }
}
