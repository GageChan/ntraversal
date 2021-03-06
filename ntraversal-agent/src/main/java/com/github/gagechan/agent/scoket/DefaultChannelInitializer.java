package com.github.gagechan.agent.scoket;

import com.github.gagechan.common.codec.Decoder;
import com.github.gagechan.common.codec.Encoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author GageChan
 */
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new Decoder());
        channel.pipeline().addLast(new Encoder());
        channel.pipeline().addLast(new DefaultChannelHandler());
    }
}
