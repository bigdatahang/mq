package com.k.mq.broker.config;

/**
 * 全局配置实体类
 * 存储MQ的全局配置信息
 * 
 * @author yihang07
 */
public class GlobalProperties {
    /** MQ主目录路径 */
    public String mqHome;

    /**
     * 获取MQ主目录
     * 
     * @return MQ主目录路径
     */
    public String getMqHome() {
        return mqHome;
    }

    /**
     * 设置MQ主目录
     * 
     * @param mqHome MQ主目录路径
     */
    public void setMqHome(String mqHome) {
        this.mqHome = mqHome;
    }
}
