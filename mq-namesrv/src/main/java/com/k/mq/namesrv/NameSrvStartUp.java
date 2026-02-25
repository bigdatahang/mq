package com.k.mq.namesrv;

import com.k.mq.namesrv.cache.CommonCache;
import com.k.mq.namesrv.core.NameSrvStarter;
import com.k.mq.namesrv.core.PropertiesLoader;

public class NameSrvStartUp {
    private static NameSrvStarter nameSrvStarter;

    public static void main(String[] args) {
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        propertiesLoader.loadProperties();
        CommonCache.setPropertiesLoader(propertiesLoader);
        nameSrvStarter = new NameSrvStarter(9090);
        nameSrvStarter.startServer();
    }
}
