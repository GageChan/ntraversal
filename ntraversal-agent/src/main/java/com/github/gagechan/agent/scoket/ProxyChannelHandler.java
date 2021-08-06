package com.github.gagechan.agent.scoket;

import java.net.InetSocketAddress;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gagechan.common.ChannelHolder;
import com.github.gagechan.common.protocol.Command;
import com.github.gagechan.common.protocol.Packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/**
 * @author GageChan
 */
public class ProxyChannelHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(ProxyChannelHandler.class);

    private final String remoteProxyChannelId;

    public ProxyChannelHandler(String remoteProxyChannelId) {
        this.remoteProxyChannelId = remoteProxyChannelId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel)ctx.channel();
        ChannelHolder.put(remoteProxyChannelId, channel);
        InetSocketAddress address = channel.remoteAddress();
        log.info("[proxy] connected to {}", address.toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // log.info("[proxy] 收到消息:{}", new String((byte[]) msg));
        Channel channel = ChannelHolder.get("agent");
        Packet m = Packet.build(remoteProxyChannelId, Command.FORWARD.code(), (byte[])msg);
        Objects.requireNonNull(channel, "The agent channel is not exists in cache pool.").writeAndFlush(m);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{} occr exception.", ctx.channel().id().asShortText(), cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("[proxy] the channel {} offline", ctx.channel().remoteAddress().toString());
        ChannelHolder.remove(remoteProxyChannelId);
        super.channelInactive(ctx);
    }
}
