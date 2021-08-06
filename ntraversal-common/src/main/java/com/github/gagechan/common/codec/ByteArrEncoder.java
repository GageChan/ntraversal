package com.github.gagechan.common.codec;

import java.util.List;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * @author GageChan
 * @see io.netty.handler.codec.bytes.ByteArrayDecoder
 */
public class ByteArrEncoder extends MessageToMessageEncoder<byte[]> {
    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        out.add(Unpooled.wrappedBuffer(msg));
    }
}