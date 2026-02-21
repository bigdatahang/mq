package com.k.mq.broker.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumeQueueMMapFileModelManager {
    private Map<String, List<ConsumeQueueMMapFileModel>> consumeQueueMMapFileModelMap = new HashMap<>();

    public void put(String topic, List<ConsumeQueueMMapFileModel> consumeQueueMMapFileModels) {
        consumeQueueMMapFileModelMap.put(topic, consumeQueueMMapFileModels);
    }

    public List<ConsumeQueueMMapFileModel> get(String topic) {
        return consumeQueueMMapFileModelMap.get(topic);
    }
}
