package com.k.mq.namesrv.cache;

import com.k.mq.namesrv.core.PropertiesLoader;
import com.k.mq.namesrv.store.ServiceInstanceManager;

public class CommonCache {
    private static PropertiesLoader propertiesLoader = new PropertiesLoader();
    private static ServiceInstanceManager serviceInstanceManager = new ServiceInstanceManager();

    public static ServiceInstanceManager getServiceInstanceManager() {
        return serviceInstanceManager;
    }

    public static void setServiceInstanceManager(ServiceInstanceManager serviceInstanceManager) {
        CommonCache.serviceInstanceManager = serviceInstanceManager;
    }

    public static PropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }

    public static void setPropertiesLoader(PropertiesLoader propertiesLoader) {
        CommonCache.propertiesLoader = propertiesLoader;
    }
}
