package com.github.gagechan.ntserver.socket;

import com.github.gagechan.common.codec.ByteArrDecoder;
import com.github.gagechan.common.codec.ByteArrEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author GageChan
 */
public class ProxyChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new ByteArrDecoder());
        socketChannel.pipeline().addLast(new ByteArrEncoder());
        socketChannel.pipeline().addLast(new ProxyChannelHandler());
    }
}
