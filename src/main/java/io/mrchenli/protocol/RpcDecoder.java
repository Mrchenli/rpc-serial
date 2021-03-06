package io.mrchenli.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * in
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    /**
     * @param ctx channel handler
     * @param in request
     * @param out response
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
        if(in.readableBytes()<4){
            return;
        }
        in.markReaderIndex();//todo
        int dataLength = in.readInt();
        if(in.readableBytes()<dataLength){
             in.resetReaderIndex();
             return;
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Serializer serializer= new JsonSerializer();
        Object obj = serializer.deserialize(data,genericClass);
        out.add(obj);
    }
}
