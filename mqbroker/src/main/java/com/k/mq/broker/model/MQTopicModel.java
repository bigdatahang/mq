package com.k.mq.broker.model;

import java.util.List;

public class MQTopicModel {
    private String topic;
    private List<QueueModel> queueList;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<QueueModel> getQueueList() {
        return queueList;
    }

    public void setQueueList(List<QueueModel> queueList) {
        this.queueList = queueList;
    }

    @Override
    public String toString() {
        return "MQTopicModel{" +
                "topic='" + topic + '\'' +
                ", queueList=" + queueList +
                '}';
    }
}
