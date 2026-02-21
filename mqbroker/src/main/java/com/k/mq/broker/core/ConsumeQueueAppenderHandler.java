package com.k.mq.broker.core;

import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.MQTopicModel;
import com.k.mq.broker.model.QueueModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsumeQueueAppenderHandler {
    private ConsumeQueueMMapFileModelManager consumeQueueMMapFileModelManager = new ConsumeQueueMMapFileModelManager();

    public void prepareConsumeQueueMMapLoading(String topic) throws IOException {
        MQTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topic);
        List<QueueModel> queueList = mqTopicModel.getQueueList();
        List<ConsumeQueueMMapFileModel> consumeQueueMMapFileModelList = new ArrayList<>();
        for (QueueModel queueModel : queueList) {
            ConsumeQueueMMapFileModel consumeQueueMMapFileModel = new ConsumeQueueMMapFileModel();
            consumeQueueMMapFileModel.loadFileInMMap(topic,
                    queueModel.getId(),
                    queueModel.getFileName(),
                    queueModel.getLastOffset(),
                    queueModel.getOffsetLimit()
            );
            consumeQueueMMapFileModelList.add(consumeQueueMMapFileModel);
        }
        consumeQueueMMapFileModelManager.put(topic, consumeQueueMMapFileModelList);
    }
}
