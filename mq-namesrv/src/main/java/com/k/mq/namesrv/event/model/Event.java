package com.k.mq.namesrv.event.model;

import io.netty.channel.ChannelHandlerContext;

public abstract class Event {
    private long timestamp;
    private ChannelHandlerContext channelHandlerContext;
}
