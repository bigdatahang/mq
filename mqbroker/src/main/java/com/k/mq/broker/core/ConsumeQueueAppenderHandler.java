package com.k.mq.broker.core;

import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.MQTopicModel;
import com.k.mq.broker.model.QueueModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ConsumeQueue追加处理器
 * 负责管理Topic的ConsumeQueue文件映射和消息索引的读写操作
 *
 * @author yihang07
 */
public class ConsumeQueueAppenderHandler {
    /**
     * 准备ConsumeQueue文件的内存映射加载
     * 为指定Topic的所有队列创建内存映射
     *
     * @param topic 主题名称
     * @throws IOException 当文件操作失败时抛出
     */
    public void prepareConsumeQueueMMapLoading(String topic) throws IOException {
        MQTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topic);
        if (mqTopicModel == null) {
            throw new IllegalArgumentException("topic does not exist, topic is: " + topic);
        }

        List<QueueModel> queueList = mqTopicModel.getQueueList();
        if (queueList == null || queueList.isEmpty()) {
            throw new IllegalStateException("queueList is empty for topic: " + topic);
        }

        List<ConsumeQueueMMapFileModel> consumeQueueMMapFileModelList = new ArrayList<>();

        // 为每个队列创建内存映射
        for (QueueModel queueModel : queueList) {
            ConsumeQueueMMapFileModel consumeQueueMMapFileModel = new ConsumeQueueMMapFileModel();
            consumeQueueMMapFileModel.loadFileInMMap(
                    topic,
                    queueModel.getId(),
                    queueModel.getLastOffset(),
                    queueModel.getOffsetLimit()
            );
            consumeQueueMMapFileModelList.add(consumeQueueMMapFileModel);
        }
        CommonCache.getConsumeQueueMMapFileModelManager().put(topic, consumeQueueMMapFileModelList);
    }
}
