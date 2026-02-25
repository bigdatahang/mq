package com.k.mq.namesrv.event.spi.listener;

import com.k.mq.common.coder.TcpMessage;
import com.k.mq.common.enums.NameSrvResponseCode;
import com.k.mq.namesrv.cache.CommonCache;
import com.k.mq.namesrv.event.model.RegistryEvent;
import com.k.mq.namesrv.store.ServiceInstance;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.UUID;

public class RegistryListener implements Listener<RegistryEvent> {
    @Override
    public void onReceive(RegistryEvent event) throws IllegalArgumentException {
        // 权限校验
        String username = event.getUsername();
        String password = event.getPassword();
        ChannelHandlerContext channelHandlerContext = event.getChannelHandlerContext();
        channelHandlerContext.attr(AttributeKey.valueOf("reqId")).set(UUID.randomUUID().toString());
        if (!username.equals(CommonCache.getPropertiesLoader().getProperty("namesrv.username"))
                || !password.equals(CommonCache.getPropertiesLoader().getProperty("namesrv.password"))) {
            TcpMessage message = new TcpMessage(NameSrvResponseCode.ERROR_USER_OR_PASSWORD.getCode(), NameSrvResponseCode.ERROR_USER_OR_PASSWORD.getDesc().getBytes());
            channelHandlerContext.writeAndFlush(message);
            channelHandlerContext.close();
            throw new IllegalArgumentException("error account to connected!");
        }
        channelHandlerContext.attr(AttributeKey.valueOf("reqId")).set(UUID.randomUUID().toString());
        ServiceInstance serviceInstance = new ServiceInstance();
        String brokerIp = event.getBrokerIp();
        Integer brokerPort = event.getBrokerPort();
        serviceInstance.setBrokerIp(brokerIp);
        serviceInstance.setBrokerPort(brokerPort);
        serviceInstance.setFirstRegistryTime(System.currentTimeMillis());
        CommonCache.getServiceInstanceManager().put(serviceInstance);
    }
}
