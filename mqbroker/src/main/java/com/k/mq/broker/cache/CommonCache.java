package com.k.mq.broker.cache;

import com.k.mq.broker.config.GlobalProperties;
import com.k.mq.broker.model.MQTopicModel;

import java.util.List;

/**
 * 全局缓存类
 * 用于缓存Broker运行时的全局配置和Topic配置信息
 * 
 * @author yihang07
 */
public class CommonCache {
    /** 全局配置对象 */
    public static GlobalProperties globalProperties;
    /** MQ主题模型列表 */
    public static List<MQTopicModel> mqTopicModelList;

    /**
     * 获取全局配置
     * 
     * @return 全局配置对象
     */
    public static GlobalProperties  getGlobalProperties() {
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

    /**
     * 获取MQ主题模型列表
     * 
     * @return MQ主题模型列表
     */
    public static List<MQTopicModel> getMqTopicModelList() {
        return mqTopicModelList;
    }

    /**
     * 设置MQ主题模型列表
     * 
     * @param mqTopicModelList MQ主题模型列表
     */
    public static void setMqTopicModelList(List<MQTopicModel> mqTopicModelList) {
        CommonCache.mqTopicModelList = mqTopicModelList;
    }
}
