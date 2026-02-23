package com.k.mq.namesrv.test;

import com.k.mq.common.coder.TcpMessage;
import com.k.mq.common.coder.TcpMessageDecoder;
import com.k.mq.common.coder.TcpMessageEncoder;
import com.k.mq.namesrv.handler.NameSrvResponseChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestNameSrvSuite {

    private EventLoopGroup clientGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private String DEFAULT_NAMESRV_IP = "127.0.0.1";

    @Before
    public void setup() {
        bootstrap.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new TcpMessageDecoder());
                        ch.pipeline().addLast(new TcpMessageEncoder());
                        ch.pipeline().addLast(new NameSrvResponseChannelHandler());
                    }
                });
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(DEFAULT_NAMESRV_IP, 9090).sync();
            channel = channelFuture.channel();
            System.out.println("success connect to namesrv");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            System.out.println("isActive:" + channel.isActive());
            TimeUnit.SECONDS.sleep(1); // 必须要sleep一会，否则消息异步发送后，测试方法立即结束，导致消息还没真正发送到服务器，EventLoopGroup 就被关闭了。
            String msgBody = "this is a content " + i;
            TcpMessage message = new TcpMessage(1, msgBody.getBytes());
            channel.writeAndFlush(message);
        }
    }
}
