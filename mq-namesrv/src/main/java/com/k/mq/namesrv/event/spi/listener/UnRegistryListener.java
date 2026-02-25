package com.k.mq.namesrv.event.spi.listener;

import com.k.mq.common.coder.TcpMessage;
import com.k.mq.common.enums.NameSrvResponseCode;
import com.k.mq.namesrv.cache.CommonCache;
import com.k.mq.namesrv.event.model.UnRegistryEvent;
import com.k.mq.namesrv.store.ServiceInstanceManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.internal.StringUtil;

public class UnRegistryListener implements Listener<UnRegistryEvent> {
    @Override
    public void onReceive(UnRegistryEvent event) {
        String brokerIp = event.getBrokerIp();
        Integer brokerPort = event.getBrokerPort();
        ChannelHandlerContext channelHandlerContext = event.getChannelHandlerContext();
        if (channelHandlerContext.attr(AttributeKey.valueOf("reqId")).get() == null) {
            TcpMessage message = new TcpMessage(NameSrvResponseCode.ERROR_USER_OR_PASSWORD.getCode(), NameSrvResponseCode.ERROR_USER_OR_PASSWORD.getDesc().getBytes());
            channelHandlerContext.writeAndFlush(message);
            channelHandlerContext.close();
            throw new IllegalArgumentException("error account to connected!");
        }
        if (!StringUtil.isNullOrEmpty(brokerIp) && brokerPort != null) {
            ServiceInstanceManager serviceInstanceManager = CommonCache.getServiceInstanceManager();
            boolean removeStatus = serviceInstanceManager.remove(brokerIp, brokerPort) != null;
        }
    }
}
