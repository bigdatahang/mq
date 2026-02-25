package com.k.mq.namesrv.handler;

import com.alibaba.fastjson2.JSON;
import com.k.mq.common.coder.TcpMessage;
import com.k.mq.common.enums.NameSrvEventCode;
import com.k.mq.namesrv.event.EventBus;
import com.k.mq.namesrv.event.model.Event;
import com.k.mq.namesrv.event.model.HeartbeatEvent;
import com.k.mq.namesrv.event.model.RegistryEvent;
import com.k.mq.namesrv.event.model.UnRegistryEvent;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class TcpNettyServerHandler extends SimpleChannelInboundHandler {
    // 1.网络请求接收
    // 2.事件发布器的实现 EventBus => event
    // 3.事件处理器的接收 Listener => 处理event
    // 4.数据存储（基于本地MAP）
    private EventBus eventBus;

    public TcpNettyServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        TcpMessage message = (TcpMessage) msg;
        int code = message.getCode();
        byte[] body = message.getBody();
        Event event = null;
        if (code == NameSrvEventCode.REGISTRY.getCode()) {
            // 注册事件
            event = JSON.parseObject(body, RegistryEvent.class);
        } else if (code == NameSrvEventCode.UN_REGISTRY.getCode()) {
            // 下线事件
            event = JSON.parseObject(body, UnRegistryEvent.class);
        } else if (code == NameSrvEventCode.HEARTBEAT.getCode()) {
            // 心跳事件
            event = JSON.parseObject(body, HeartbeatEvent.class);
        }
        eventBus.publish(event);
    }
}
