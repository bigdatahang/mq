package com.k.mq.broker.constants;

/**
 * Broker常量定义类
 * 定义Broker运行时使用的各种常量
 *
 * @author yihang07
 */
public class BrokerConstants {
    /** MQ主目录环境变量名 */
    public static final String MQ_HOME = "MQ_HOME";
    /** 基础存储路径 */
    public static final String BASE_STORE_PATH = "/broker/store/";
    public static final Integer COMMIT_LOG_DEFAULT_MAPPED_SIZE = 1 * 1024 * 1024;
}
