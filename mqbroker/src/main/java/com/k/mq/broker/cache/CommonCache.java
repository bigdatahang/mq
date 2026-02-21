package com.k.mq.broker.cache;

import com.k.mq.broker.config.GlobalProperties;
import com.k.mq.broker.core.ConsumeQueueMMapFileModelManager;
import com.k.mq.broker.model.ConsumeQueueOffsetModel;
import com.k.mq.broker.model.MQTopicModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public static GlobalProperties globalProperties = new GlobalProperties();

    public static List<MQTopicModel> mqTopicModelList = new ArrayList<>();

    public static ConsumeQueueOffsetModel consumeQueueOffsetModel = new ConsumeQueueOffsetModel();

    public static ConsumeQueueMMapFileModelManager consumeQueueMMapFileModelManager = new ConsumeQueueMMapFileModelManager();

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
        return mqTopicModelList.stream().collect(Collectors.toMap(MQTopicModel::getTopic, item -> item));
    }

    public static void setMqTopicModelList(List<MQTopicModel> mqTopicModelList) {
        CommonCache.mqTopicModelList = mqTopicModelList;
    }

    public static List<MQTopicModel> getMqTopicModelList() {
        return mqTopicModelList;
    }

    public static ConsumeQueueOffsetModel getConsumeQueueOffsetModel() {
        return consumeQueueOffsetModel;
    }

    public static void setConsumeQueueOffsetModel(ConsumeQueueOffsetModel consumeQueueOffsetModel) {
        CommonCache.consumeQueueOffsetModel = consumeQueueOffsetModel;
    }

    public static ConsumeQueueMMapFileModelManager getConsumeQueueMMapFileModelManager() {
        return consumeQueueMMapFileModelManager;
    }

    public static void setConsumeQueueMMapFileModelManager(ConsumeQueueMMapFileModelManager consumeQueueMMapFileModelManager) {
        CommonCache.consumeQueueMMapFileModelManager = consumeQueueMMapFileModelManager;
    }
}
