package com.gagechan.agent.scoket;

import com.gagechan.common.codec.ByteArrDecoder;
import com.gagechan.common.codec.ByteArrEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author GageChan
 */
public class ProxyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final String remoteProxyChannelId;

    public ProxyChannelInitializer(String remoteProxyChannelId) {
        this.remoteProxyChannelId = remoteProxyChannelId;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new ByteArrDecoder());
        channel.pipeline().addLast(new ByteArrEncoder());
        channel.pipeline().addLast(new ProxyChannelHandler(remoteProxyChannelId));
    }
}
