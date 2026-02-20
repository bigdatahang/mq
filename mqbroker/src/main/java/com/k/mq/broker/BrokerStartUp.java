package com.k.mq.broker;

import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.config.GlobalPropertiesLoader;
import com.k.mq.broker.config.MQTopicLoader;
import com.k.mq.broker.core.MessageAppenderHandler;
import com.k.mq.broker.model.MQTopicModel;

import java.io.IOException;
import java.util.List;

import static com.k.mq.broker.constants.BrokerConstants.BASE_STORE_PATH;

public class BrokerStartUp {
    public static MQTopicLoader mqTopicLoader;
    public static GlobalPropertiesLoader globalPropertiesLoader;
    public static MessageAppenderHandler messageAppenderHandler;

    public static void initProperties() throws IOException {
        globalPropertiesLoader = new GlobalPropertiesLoader();
        globalPropertiesLoader.loadProperties();
        mqTopicLoader = new MQTopicLoader();
        mqTopicLoader.loadProperties();
        messageAppenderHandler = new MessageAppenderHandler();
        List<MQTopicModel> mqTopicModelList = CommonCache.getMqTopicModelList();
        for (MQTopicModel mqTopicModel : mqTopicModelList) {
            String topic = mqTopicModel.getTopic();
            String filePath = CommonCache.getGlobalProperties().getMqHome()
                    + BASE_STORE_PATH
                    + topic
                    + "/00000001";
            messageAppenderHandler.prepareMMapLoading(filePath, topic);
        }
    }

    public static void main(String[] args) throws IOException {
        initProperties();
        String topic = "order_cancel_topic";
        messageAppenderHandler.appendMessage(topic, "this is a new test");
        messageAppenderHandler.readContent(topic);
    }
}
