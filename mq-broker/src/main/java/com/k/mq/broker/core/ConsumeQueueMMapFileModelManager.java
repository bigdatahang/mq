package com.k.mq.broker.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ConsumeQueue内存映射文件模型管理器
 * 管理所有Topic的ConsumeQueue文件映射
 * 
 * <p>数据结构：Map<Topic名称, List<ConsumeQueueMMapFileModel>>
 * <p>每个Topic可能有多个Queue，每个Queue对应一个ConsumeQueueMMapFileModel
 *
 * @author yihang07
 */
public class ConsumeQueueMMapFileModelManager {
    /**
     * ConsumeQueue映射管理容器
     * Key: Topic名称
     * Value: 该Topic下所有Queue的内存映射文件模型列表
     */
    private Map<String, List<ConsumeQueueMMapFileModel>> consumeQueueMMapFileModelMap = new HashMap<>();

    /**
     * 添加Topic的ConsumeQueue映射列表
     *
     * @param topic                      主题名称
     * @param consumeQueueMMapFileModels ConsumeQueue映射文件模型列表
     */
    public void put(String topic, List<ConsumeQueueMMapFileModel> consumeQueueMMapFileModels) {
        consumeQueueMMapFileModelMap.put(topic, consumeQueueMMapFileModels);
    }

    /**
     * 获取Topic的ConsumeQueue映射列表
     *
     * @param topic 主题名称
     * @return ConsumeQueue映射文件模型列表，如果不存在则返回null
     */
    public List<ConsumeQueueMMapFileModel> get(String topic) {
        return consumeQueueMMapFileModelMap.get(topic);
    }

    /**
     * 获取指定Topic和QueueId的ConsumeQueue映射模型
     *
     * @param topic   主题名称
     * @param queueId 队列ID
     * @return ConsumeQueue映射文件模型，如果不存在则返回null
     */
    public ConsumeQueueMMapFileModel get(String topic, int queueId) {
        List<ConsumeQueueMMapFileModel> models = consumeQueueMMapFileModelMap.get(topic);
        if (models == null || queueId >= models.size()) {
            return null;
        }
        return models.get(queueId);
    }
}
