package com.k.mq.broker.core;

import java.util.HashMap;
import java.util.Map;

public class MMapFileModelManager {
    private Map<String, MMapFileModel> mmapFileModelMap = new HashMap<>();

    public void put(String topic, MMapFileModel mmapFileModel) {
        mmapFileModelMap.put(topic, mmapFileModel);
    }

    public MMapFileModel get(String topic) {
        return mmapFileModelMap.get(topic);
    }
}
