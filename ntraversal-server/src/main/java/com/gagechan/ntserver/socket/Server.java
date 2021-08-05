package com.gagechan.ntserver.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;

/**
 * @author GageChan
 */
public class Server {

    private final Logger log = LoggerFactory.getLogger(Server.class);

    private Channel channel;

    public void start(ChannelInitializer<SocketChannel> initializer, int port) {
        log.info("starting {}", port);
        final EventLoopGroup parentGroup = new NioEventLoopGroup();
        final EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(initializer);

            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
            channel = b.bind(port).sync().channel();
        } catch (Exception e) {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
            log.error("socket server start error.", e);
        } finally {
            if (channel.isActive()) {
                log.info("socket server started at {}", channel.localAddress());
            } else {
                log.info("socket server start failed.");
            }
        }
    }

    public void destroy() {
        if (channel == null) return;
        this.channel.close();
    }

}
