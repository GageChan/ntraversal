package com.github.gagechan.agent.scoket;

import java.net.InetSocketAddress;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gagechan.agent.AppConfig;
import com.github.gagechan.common.ChannelHolder;
import com.github.gagechan.common.Runner;
import com.github.gagechan.common.protocol.Command;
import com.github.gagechan.common.protocol.Packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/**
 * @author GageChan
 */
public class DefaultChannelHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(DefaultChannelHandler.class);

    private Thread heartBeatThread;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel)ctx.channel();
        InetSocketAddress address = channel.remoteAddress();
        log.info("[agent] connected to {}", address.toString());
        ChannelHolder.put("agent", channel);
        heartBeatThread = new Thread(() -> {
           while (true) {
               Runner.wait(5);
               Packet packet = Packet.build(Command.HEARTBEAT.code(), Packet.emptyData());
               ctx.channel().writeAndFlush(packet);
           }
        });
        heartBeatThread.start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // log.info("[agent] 收到消息:{}", msg);
        if (msg instanceof Packet) {
            Packet packet = (Packet)msg;
            if (packet.getCommand() == Command.START_AGENT_PROXY.code() || packet.getCommand() == Command.FORWARD.code()) {
                Channel channel = ChannelHolder.get(packet.getRemoteProxyChannelId());
                if (channel == null || packet.getCommand() == Command.START_AGENT_PROXY.code()) {
                    ChannelHolder.remove("proxy");
                    // 创建代理监控
                    new Thread(() -> {
                        Client client = new Client();
                        client.connect(new ProxyChannelInitializer(packet.getRemoteProxyChannelId()), "127.0.0.1", AppConfig.proxyPort());
                    }, "proxy").start();
                    Runner.runWithTimeout(() -> {
                        Channel ch = ChannelHolder.get(packet.getRemoteProxyChannelId());
                        return ch != null;
                    }, 5000);
                    if (packet.getCommand() == Command.START_AGENT_PROXY.code()) {
                        return;
                    }
                }
                channel = ChannelHolder.get(packet.getRemoteProxyChannelId());
                byte[] data = packet.getData();
                Objects.requireNonNull(channel, "The proxy channel is not exists in cache pool.").writeAndFlush(data);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{} occr exception.", ctx.channel().id().asShortText(), cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (!heartBeatThread.isInterrupted()) {
            heartBeatThread.interrupt();
            heartBeatThread = null;
        }
        log.info("[agent] the channel {} offline, now reconnecting...", ctx.channel().remoteAddress().toString());
        Client client = new Client();
        client.connect(new DefaultChannelInitializer(), AppConfig.serverHost(), AppConfig.serverPort());
        super.channelInactive(ctx);
    }
}
