package com.k.mq.broker.config;

import com.k.mq.broker.cache.CommonCache;
import io.netty.util.internal.StringUtil;

/**
 * 全局配置加载器
 * 负责加载MQ的全局配置信息，如MQ_HOME等
 * 
 * @author yihang07
 */
public class GlobalPropertiesLoader {

    /**
     * 加载全局配置
     * 从环境变量或默认配置中加载MQ_HOME等全局配置信息
     */
    public void loadProperties() {
        GlobalProperties globalProperties = new GlobalProperties();
        // String mqHome = System.getenv(MQ_HOME);
        String mqHome = "/Users/yihang07/code/mq";
        if (StringUtil.isNullOrEmpty(mqHome)) {
            throw new IllegalArgumentException("MQ_HOME IS NULL");
        }
        globalProperties.setMqHome(mqHome);
        CommonCache.setGlobalProperties(globalProperties);
    }
}
