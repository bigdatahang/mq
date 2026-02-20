package com.k.mq.broker.model;

import java.util.List;

/**
 * MQ主题模型
 * 表示一个消息队列主题的配置信息
 * 
 * @author yihang07
 */
public class MQTopicModel {
    /** 主题名称 */
    private String topic;
    /** 队列列表 */
    private List<QueueModel> queueList;

    /**
     * 获取主题名称
     * 
     * @return 主题名称
     */
    public String getTopic() {
        return topic;
    }

    /**
     * 设置主题名称
     * 
     * @param topic 主题名称
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * 获取队列列表
     * 
     * @return 队列列表
     */
    public List<QueueModel> getQueueList() {
        return queueList;
    }

    /**
     * 设置队列列表
     * 
     * @param queueList 队列列表
     */
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
