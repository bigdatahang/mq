package com.k.mq.namesrv.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {
    private Properties properties = new Properties(); 

    public void loadProperties() {
        // String mqHome = System.getenv(MQ_HOME);
        String mqHome = "/Users/yihang07/code/mq";
        try (FileInputStream fis = new FileInputStream(mqHome + "/broker/config/namesrv.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
