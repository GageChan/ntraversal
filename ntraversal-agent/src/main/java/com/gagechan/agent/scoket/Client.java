package com.gagechan.agent.scoket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ResourceLeakDetector;

/**
 * @author GageChan
 */
public class Client {

    private final static Logger log = LoggerFactory.getLogger(Client.class);


    public void connect(ChannelInitializer<SocketChannel> initializer, String inetHost, int inetPort) {
        log.info("Is connecting to {}...", inetPort);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(initializer);
            ChannelFuture f = b.connect(inetHost, inetPort).sync();
            f.channel().closeFuture().sync();
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
