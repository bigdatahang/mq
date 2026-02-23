package com.k.mq.namesrv.core;

import com.k.mq.common.coder.TcpMessageDecoder;
import com.k.mq.common.coder.TcpMessageEncoder;
import com.k.mq.namesrv.handler.TcpNettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NameSrvStarter {
    int port;

    public NameSrvStarter(int port) {
        this.port = port;
    }

    public void startServer() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)  // 指定 NIO 传输通道
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new TcpMessageDecoder());
                            ch.pipeline().addLast(new TcpMessageEncoder());
                            ch.pipeline().addLast(new TcpNettyServerHandler());
                        }
                    });

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }));

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            System.out.println("NameServer started on port: " + port);

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
