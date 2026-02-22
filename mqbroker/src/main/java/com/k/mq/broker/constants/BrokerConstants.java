package com.k.mq.broker.constants;

/**
 * Broker常量定义类
 * 定义Broker运行时使用的各种常量
 *
 * @author yihang07
 */
public class BrokerConstants {
    /**
     * MQ主目录环境变量名
     */
    public static final String MQ_HOME = "MQ_HOME";
    /**
     * 基础存储路径
     */
    public static final String BASE_COMMIT_LOG_PATH = "/broker/commitlog/";
    public static final String BASE_CONSUME_QUEUE_PATH = "/broker/consumequeue/";
    public static final String SPLIT = "/";
    public static final Integer COMMIT_LOG_DEFAULT_MAPPED_SIZE = 1 * 1024 * 1024;
    public static final Long DEFAULT_REFRESH_MQ_TOPIC_SECOND = 3L;
    public static final Long DEFAULT_REFRESH_CONSUME_QUEUE_OFFSET_SECOND = 3L;

}
