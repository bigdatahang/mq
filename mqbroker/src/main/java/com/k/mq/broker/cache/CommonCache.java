package com.k.mq.broker.cache;

import com.k.mq.broker.config.GlobalProperties;
import com.k.mq.broker.model.MQTopicModel;

import java.util.List;

public class CommonCache {
    public static GlobalProperties globalProperties;
    public static List<MQTopicModel> mqTopicModelList;

    public static GlobalProperties  getGlobalProperties() {
        return globalProperties;
    }

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.globalProperties = globalProperties;
    }

    public static List<MQTopicModel> getMqTopicModelList() {
        return mqTopicModelList;
    }

    public static void setMqTopicModelList(List<MQTopicModel> mqTopicModelList) {
        CommonCache.mqTopicModelList = mqTopicModelList;
    }
}
