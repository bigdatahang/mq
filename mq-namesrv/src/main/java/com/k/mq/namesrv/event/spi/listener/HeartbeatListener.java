package com.k.mq.namesrv.event.spi.listener;

import com.k.mq.common.coder.TcpMessage;
import com.k.mq.common.enums.NameSrvResponseCode;
import com.k.mq.namesrv.cache.CommonCache;
import com.k.mq.namesrv.event.model.HeartbeatEvent;
import com.k.mq.namesrv.store.ServiceInstance;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class HeartbeatListener implements Listener<HeartbeatEvent> {
    @Override
    public void onReceive(HeartbeatEvent event) throws IllegalArgumentException {
        ChannelHandlerContext channelHandlerContext = event.getChannelHandlerContext();
        if (channelHandlerContext.attr(AttributeKey.valueOf("reqId")).get() == null) {
            TcpMessage message = new TcpMessage(NameSrvResponseCode.ERROR_USER_OR_PASSWORD.getCode(), NameSrvResponseCode.ERROR_USER_OR_PASSWORD.getDesc().getBytes());
            channelHandlerContext.writeAndFlush(message);
            channelHandlerContext.close();
            throw new IllegalArgumentException("error account to connected!");
        }
        ServiceInstance serviceInstance = new ServiceInstance();
        String brokerIp = event.getBrokerIp();
        Integer brokerPort = event.getBrokerPort();
        serviceInstance.setBrokerIp(brokerIp);
        serviceInstance.setBrokerPort(brokerPort);
        serviceInstance.setLastHeartbeatTime(System.currentTimeMillis());
        CommonCache.getServiceInstanceManager().putIfExist(serviceInstance);
    }
}
