package com.k.mq.broker.cache;

import com.k.mq.broker.config.GlobalProperties;

public class CommonCache {
    public static GlobalProperties globalProperties;

    public static GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.globalProperties = globalProperties;
    }
}
