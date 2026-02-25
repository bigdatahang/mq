package com.k.mq.namesrv.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceInstanceManager {
    private Map<String, ServiceInstance> serviceInstanceMap = new ConcurrentHashMap<>();

    public void put(ServiceInstance serviceInstance) {
        String brokerIp = serviceInstance.getBrokerIp();
        Integer brokerPort = serviceInstance.getBrokerPort();
        serviceInstanceMap.put(brokerIp + ":" + brokerPort, serviceInstance);
    }

    public ServiceInstance get(String brokerIp, Integer brokerPort) {
        return serviceInstanceMap.get(brokerIp + ":" + brokerPort);
    }

    public void putIfExist(ServiceInstance serviceInstance) {
        ServiceInstance currentInstance = this.get(serviceInstance.getBrokerIp(), serviceInstance.getBrokerPort());
        if (currentInstance != null && currentInstance.getFirstRegistryTime() != null) {
            serviceInstance.setFirstRegistryTime(currentInstance.getFirstRegistryTime());
        }
        String brokerIp = serviceInstance.getBrokerIp();
        Integer brokerPort = serviceInstance.getBrokerPort();
        serviceInstanceMap.put(brokerIp + ":" + brokerPort, serviceInstance);
    }

    public ServiceInstance remove(String brokerIp, Integer brokerPort) {
        return serviceInstanceMap.remove(brokerIp + ":" + brokerPort);
    }
}
