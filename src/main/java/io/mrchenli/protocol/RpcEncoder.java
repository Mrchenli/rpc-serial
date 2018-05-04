package io.mrchenli.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 把一个对象编码成字节流 out
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf byteBuf)
            throws Exception {
        if(genericClass.isInstance(in)){
            byte[] data = null ;//这里序列化成字节数组
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);//到下一个handler了
        }
    }


}
