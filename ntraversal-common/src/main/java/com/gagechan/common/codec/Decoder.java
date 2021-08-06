package com.gagechan.common.codec;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.gagechan.common.protocol.FlagSymbols;
import com.gagechan.common.protocol.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.StringUtil;

/**
 * @author GageChan
 */
public class Decoder extends ByteToMessageDecoder {

    private static final int MIN_PACKET_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf bf, List<Object> out) throws Exception {
        if (bf.readableBytes() < MIN_PACKET_LENGTH) {
            return;
        }
        int originIdx;

        while (true) {
            // 获取包头开始的index
            originIdx = bf.readerIndex();
            // 标记包头开始的index
            bf.markReaderIndex();
            // 读到了协议的开始标志，结束while循环
            if (bf.readByte() == FlagSymbols.START) {
                break;
            }
            // 未读到包头，略过一个字节
            // 每次略过，一个字节，去读取，包头信息的开始标记
            bf.resetReaderIndex();
            bf.readByte();
            // 当略过，一个字节之后，
            // 数据包的长度，又变得不满足
            // 此时，应该结束。等待后面的数据到达
            if (bf.readableBytes() < MIN_PACKET_LENGTH) {
                return;
            }
        }

        // 剩余长度不足可读取数量[没有内容长度位]
        int readableCount = bf.readableBytes();
        if (readableCount <= 1) {
            bf.readerIndex(originIdx);
            return;
        }

        byte command = bf.readByte();

        // 读取data
        int msgLength = bf.readInt();
        if (bf.readableBytes() < msgLength) {
            bf.readerIndex(originIdx);
            return;
        }
        ByteBuf msgContent = bf.readBytes(msgLength);

        // 读取服务端代理通道id
        int remoteProxyChannelIdLength = bf.readInt();
        if (bf.readableBytes() < remoteProxyChannelIdLength + 1) {
            bf.readerIndex(originIdx);
            return;
        }
        String remoteProxyChannel = null;
        if (remoteProxyChannelIdLength > 0) {
            ByteBuf remoteProxyChannelIdBf = bf.readBytes(remoteProxyChannelIdLength);
            remoteProxyChannel = remoteProxyChannelIdBf.toString(StandardCharsets.UTF_8);
            remoteProxyChannelIdBf.release();
        }

        // 如果没有结尾标识，还原指针位置[其他标识结尾]
        byte end = bf.readByte();
        if (end != FlagSymbols.END) {
            bf.readerIndex(originIdx);
            return;
        }
        // 构造data
        byte[] b = new byte[msgLength];
        msgContent.readBytes(b);
        msgContent.release();
        Packet packet;
        if (StringUtil.isNullOrEmpty(remoteProxyChannel)) {
            packet = Packet.build(command, b);
        } else {
            packet = Packet.build(remoteProxyChannel, command, b);
        }
        out.add(packet);
    }

}
