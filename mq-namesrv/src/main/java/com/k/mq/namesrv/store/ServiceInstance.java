package com.k.mq.namesrv.store;

import java.util.HashMap;
import java.util.Map;

public class ServiceInstance {
    private String brokerIp;
    private Integer brokerPort;
    private Long firstRegistryTime;
    private Long lastHeartbeatTime;
    private Map<String, String> attrs = new HashMap<>();

    public String getBrokerIp() {
        return brokerIp;
    }

    public void setBrokerIp(String brokerIp) {
        this.brokerIp = brokerIp;
    }

    public Integer getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(Integer brokerPort) {
        this.brokerPort = brokerPort;
    }

    public Long getFirstRegistryTime() {
        return firstRegistryTime;
    }

    public void setFirstRegistryTime(Long firstRegistryTime) {
        this.firstRegistryTime = firstRegistryTime;
    }

    public Long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(Long lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }
}
