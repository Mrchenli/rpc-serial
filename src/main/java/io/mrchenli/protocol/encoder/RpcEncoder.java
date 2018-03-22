package io.mrchenli.protocol.encoder;

import io.mrchenli.protocol.request.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 1.如何把一个对象写出去
 */
public class RpcEncoder extends MessageToByteEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf)
            throws Exception {



    }


}
