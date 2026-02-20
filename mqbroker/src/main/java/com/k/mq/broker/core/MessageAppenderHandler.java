package com.k.mq.broker.core;

import java.io.IOException;

public class MessageAppenderHandler {
    public MMapFileModelManager mmapFileModelManager = new MMapFileModelManager();

    public void prepareMMapLoading(String filePath, String topic) throws IOException {
        MMapFileModel mmapFileModel = new MMapFileModel();
        mmapFileModel.loadFileInMMap(filePath, 0, 1 * 1024 * 1024);
        mmapFileModelManager.put(topic, mmapFileModel);
    }

    public void appendMessage(String topic, String content) {
        MMapFileModel mmapFileModel = mmapFileModelManager.get(topic);
        if (mmapFileModel == null) {
            throw new RuntimeException("topic does not exist");
        }
        mmapFileModel.writeContent(content.getBytes());
    }

    public void readContent(String topic) {
        MMapFileModel mmapFileModel = mmapFileModelManager.get(topic);
        if (mmapFileModel == null) {
            throw new RuntimeException("topic does not exist");
        }
        byte[] content = mmapFileModel.readContent(0, 10);
        System.out.println(new String(content));
    }
}
