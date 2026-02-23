package com.k.mq.namesrv.handler;

import com.k.mq.common.coder.TcpMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NameSrvResponseChannelHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        TcpMessage message = (TcpMessage) msg;
        ctx.writeAndFlush(message);
    }
}
