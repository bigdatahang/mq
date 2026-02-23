package com.k.mq.common.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.k.mq.common.constants.BrokerConstants.DEFAULT_MAGIC_NUM;
import static com.k.mq.common.constants.BrokerConstants.DEFAULT_MESSAGE_LENGTH;

public class TcpMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() > DEFAULT_MESSAGE_LENGTH) {
            short magic = in.readShort();
            if (magic != DEFAULT_MAGIC_NUM) {
                ctx.close();
                return;
            }
            int code = in.readInt();
            int len = in.readInt();
            if (in.readableBytes() < len) {
                ctx.close();
                return;
            }
            byte[] body = new byte[len];
            in.readBytes(body);
            TcpMessage message = new TcpMessage(code, body);
            out.add(message);
        }
    }
}
