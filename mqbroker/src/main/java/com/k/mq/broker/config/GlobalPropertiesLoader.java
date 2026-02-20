package com.k.mq.broker.config;

import com.k.mq.broker.cache.CommonCache;
import io.netty.util.internal.StringUtil;

public class GlobalPropertiesLoader {
    private GlobalProperties globalProperties;

    public void loadProperties() {
        globalProperties = new GlobalProperties();
        // String mqHome = System.getenv(MQ_HOME);
        String mqHome = "/Users/yihang07/code/mq";
        if (StringUtil.isNullOrEmpty(mqHome)) {
            throw new IllegalArgumentException("MQ_HOME IS NULL");
        }
        globalProperties.setMqHome(mqHome);
        CommonCache.setGlobalProperties(globalProperties);
    }
}
