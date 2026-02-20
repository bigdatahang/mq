package com.k.mq.broker.config;

import com.alibaba.fastjson.JSON;
import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.MQTopicModel;
import com.k.mq.broker.util.FileContentReaderUtil;
import io.netty.util.internal.StringUtil;

import java.util.List;

public class MQTopicLoader {
    private List<MQTopicModel> mqTopicModelList;

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
