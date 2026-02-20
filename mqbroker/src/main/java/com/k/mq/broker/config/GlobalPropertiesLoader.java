package com.k.mq.broker.config;

import com.k.mq.broker.cache.CommonCache;
import io.netty.util.internal.StringUtil;

import static com.k.mq.broker.constants.BrokerConstants.MQ_HOME;

public class GlobalPropertiesLoader {
    private GlobalProperties globalProperties;

    public void loadProperties() {
        globalProperties = new GlobalProperties();
        String mqHome = System.getProperty(MQ_HOME);
        if (StringUtil.isNullOrEmpty(mqHome)) {
            throw new IllegalArgumentException("MQ_HOME IS NULL");
        }
        globalProperties.setMqHome(mqHome);
        CommonCache.setGlobalProperties(globalProperties);
    }
}
