package com.k.mq.broker.cache;

import com.k.mq.broker.config.GlobalProperties;
import com.k.mq.broker.model.MQTopicModel;

import java.util.Map;

/**
 * 全局缓存类
 * 用于缓存Broker运行时的全局配置和Topic配置信息
 *
 * @author yihang07
 */
public class CommonCache {
    /**
     * 全局配置对象
     */
    public static GlobalProperties globalProperties;

    public static Map<String, MQTopicModel> mqTopicModelMap;

    /**
     * 获取全局配置
     *
     * @return 全局配置对象
     */
    public static GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    /**
     * 设置全局配置
     *
     * @param globalProperties 全局配置对象
     */
    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.globalProperties = globalProperties;
    }

    public static Map<String, MQTopicModel> getMqTopicModelMap() {
        return mqTopicModelMap;
    }

    public static void setMqTopicModelMap(Map<String, MQTopicModel> mqTopicModelMap) {
        CommonCache.mqTopicModelMap = mqTopicModelMap;
    }
}
