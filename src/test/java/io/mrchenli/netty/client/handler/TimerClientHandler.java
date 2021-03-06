package io.mrchenli.netty.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * 由于socket的接收缓存区是流
 * 这个handler可能存在问题
 * 就是用户程序的4个字节的缓冲数组 可能读到的是一个整数的3个字节
 */
public class TimerClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * it is handler's duty to release reference-count obj
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf) msg;
        try {
            long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println("============"+new Date(currentTimeMillis));
            ctx.close();
        }finally {
            m.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
