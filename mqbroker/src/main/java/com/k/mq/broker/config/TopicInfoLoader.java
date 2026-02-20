package com.k.mq.broker.config;

import com.k.mq.broker.cache.CommonCache;
import io.netty.util.internal.StringUtil;

public class TopicInfoLoader {
    private TopicInfo topicInfo;

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String mqHome = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(mqHome)) {
            throw new IllegalArgumentException("MQ_HOME IS NULL");
        }
        String jsonFilePath = mqHome + "/broker/config/mq-topic.json";
        topicInfo = new TopicInfo();
    }
}
