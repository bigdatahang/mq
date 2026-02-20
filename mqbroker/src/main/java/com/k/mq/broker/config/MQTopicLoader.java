package com.k.mq.broker.config;

import com.alibaba.fastjson.JSON;
import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.MQTopicModel;
import com.k.mq.broker.util.FileContentReaderUtil;
import io.netty.util.internal.StringUtil;

import java.util.List;

/**
 * MQ主题配置加载器
 * 从配置文件中加载Topic相关的配置信息
 * 
 * @author yihang07
 */
public class MQTopicLoader {
    /** MQ主题模型列表 */
    private List<MQTopicModel> mqTopicModelList;

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
        mqTopicModelList = JSON.parseArray(FileContentReaderUtil.readFromFile(jsonFilePath), MQTopicModel.class);
        CommonCache.setMqTopicModelList(mqTopicModelList);
    }
}
