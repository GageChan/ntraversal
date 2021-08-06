package com.github.gagechan.common.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * @author GageChan
 * @see io.netty.handler.codec.bytes.ByteArrayEncoder
 */
public class ByteArrDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
         // copy the ByteBuf content to a byte array
        out.add(ByteBufUtil.getBytes(msg));
    }
}