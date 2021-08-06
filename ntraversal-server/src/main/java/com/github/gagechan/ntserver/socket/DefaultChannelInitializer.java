package com.github.gagechan.ntserver.socket;

import com.github.gagechan.common.codec.Decoder;
import com.github.gagechan.common.codec.Encoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author GageChan
 */
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new Decoder());
        socketChannel.pipeline().addLast(new Encoder());
        socketChannel.pipeline().addLast(new IdleStateHandler(6, 6, 8, TimeUnit.SECONDS));
        socketChannel.pipeline().addLast(new DefaultChannelHandler());
    }
}
