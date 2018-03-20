package io.mrchenli.netty.client.handler;

import io.mrchenli.netty.client.decoder.pojo.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * 由于socket的接收缓存区是流
 * 这个handler可能存在问题
 * 就是用户程序的4个字节的缓冲数组 可能读到的是一个整数的3个字节
 */
public class PojoTimerClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * it is handler's duty to release reference-count obj
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        UnixTime m = (UnixTime) msg;
        System.out.println("=========================="+m);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
