package com.gagechan.ntserver.socket;

import com.gagechan.common.ChannelHolder;
import com.gagechan.common.protocol.Command;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gagechan.common.protocol.Packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

/**
 * @author GageChan
 */
public class DefaultChannelHandler extends SimpleChannelInboundHandler<Packet> {
    private final static Logger log = LoggerFactory.getLogger(DefaultChannelHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ChannelHolder.put("agent", channel);
        log.info("[server] the client {} connected.", channel.remoteAddress().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ch, Packet packet) throws Exception {
        // log.info("[server]收到客户端的消息:{}", packet.getData());
        if (packet.getCommand() == Command.HEARTBEAT.code()) {
//            log.info("[server]收到客户端的心跳:{}", packet);
            ch.channel().writeAndFlush(Packet.build(Command.HEARTBEAT.code(), Packet.emptyData()));
        } else if (packet.getCommand() == Command.FORWARD.code()) {
            String remoteProxyChannelId = packet.getRemoteProxyChannelId();
            Objects
                .requireNonNull(ChannelHolder.get(remoteProxyChannelId),
                    "[agent] the proxy channel " + remoteProxyChannelId + " is not exists in cache.")
                .writeAndFlush(packet.getData());
        } else {
            log.info("unknown command:{}", packet.getCommand());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent)evt;
        if (event.state() == IdleState.ALL_IDLE) {
            log.info("失去心跳:{}", ctx.channel().remoteAddress());
            ChannelHolder.removeByChannel(ctx.channel());
            ctx.channel().close();
        }
    }
}
