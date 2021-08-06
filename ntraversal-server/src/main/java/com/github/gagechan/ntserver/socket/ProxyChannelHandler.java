package com.github.gagechan.ntserver.socket;

import java.util.Objects;

import com.github.gagechan.common.ChannelHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gagechan.common.protocol.Command;
import com.github.gagechan.common.protocol.Packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author GageChan
 */
public class ProxyChannelHandler extends SimpleChannelInboundHandler<byte[]> {
    private final static Logger log = LoggerFactory.getLogger(DefaultChannelHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ChannelHolder.put(channel.id().asShortText(), channel);
//        log.info("[proxy] the client {} connected.", channel.remoteAddress().toString());

        Channel agent = ChannelHolder.get("agent");
        Objects.requireNonNull(agent, "The agent channel is not exists in cache pool.")
            .writeAndFlush(Packet.build(channel.id().asShortText(), Command.START_AGENT_PROXY.code(), Packet.emptyData()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ch, byte[] msg) throws Exception {
        // log.info("[proxy]收到客户端的消息:{}", new String(msg));
        Channel agent = ChannelHolder.get("agent");
        Objects.requireNonNull(agent, "The agent channel is not exists in cache pool.")
            .writeAndFlush(Packet.build(ch.channel().id().asShortText(), Command.FORWARD.code(), msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChannelHolder.remove(ctx.channel().id().asShortText());
        log.info("the channel {} offline", ctx.channel().remoteAddress().toString());
        super.channelInactive(ctx);
    }
}
