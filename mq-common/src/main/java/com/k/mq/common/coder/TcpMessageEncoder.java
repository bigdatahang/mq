package com.k.mq.common.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TcpMessageEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        TcpMessage message = (TcpMessage) msg;
        out.writeShort(message.getMagic());
        out.writeInt(message.getCode());
        out.writeInt(message.getLen());
        out.writeBytes(message.getBody());
    }
}
