package com.k.mq.broker.core;

import java.io.IOException;

public class MessageAppenderHandler {
    public MMapFileModelManager mmapFileModelManager = new MMapFileModelManager();
    public static String topic = "order_cancel_topic";
    public static String filePath = "/Users/yihang07/code/mq/broker/store/order_cancel_topic/00000000";

    public MessageAppenderHandler() throws IOException {
        prepareMMapLoading();
    }

    private void prepareMMapLoading() throws IOException {
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

    private void readContent(String topic) {
        MMapFileModel mmapFileModel = mmapFileModelManager.get(topic);
        if (mmapFileModel == null) {
            throw new RuntimeException("topic does not exist");
        }
        byte[] content = mmapFileModel.readContent(0, 10);
        System.out.println(new String(content));
    }

    public static void main(String[] args) throws IOException {
        MessageAppenderHandler messageAppenderHandler = new MessageAppenderHandler();
        messageAppenderHandler.appendMessage(topic, "this is a test");
        messageAppenderHandler.readContent(topic);
    }
}
