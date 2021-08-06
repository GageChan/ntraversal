package com.github.gagechan.common.codec;

import com.github.gagechan.common.protocol.FlagSymbols;
import com.github.gagechan.common.protocol.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.StringUtil;

/**
 * @author GageChan
 * 数据包格式 |消息头|命令|消息长度|消息体|远程通道id长度|远程通道id|消息结束符号
 */
public class Encoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf out) throws Exception {
        final byte[] data = packet.getData();
        out.writeByte(FlagSymbols.START);
        out.writeByte(packet.getCommand());
        out.writeInt(data.length);

        out.writeBytes(data);
        int remoteProxyChannelIdLength = 0;
        if (!StringUtil.isNullOrEmpty(packet.getRemoteProxyChannelId())) {
            remoteProxyChannelIdLength = packet.getRemoteProxyChannelId().getBytes().length;
        }
        out.writeInt(remoteProxyChannelIdLength);
        if (!StringUtil.isNullOrEmpty(packet.getRemoteProxyChannelId())) {
            out.writeBytes(packet.getRemoteProxyChannelId().getBytes());
        }
        out.writeByte(FlagSymbols.END);
    }
}
