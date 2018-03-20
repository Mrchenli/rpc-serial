package io.mrchenli.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * invoked when a connection is established and ready to generate traffic
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        final ByteBuf time = ctx.alloc().buffer(4);
        //as usual before write flip() called ,but for netty we have two pointers one for read other for write
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
        //A ChannelFuture represents an I/O operation which has not yet occurred.
        final ChannelFuture f = ctx.writeAndFlush(time);
        f.addListener(new ChannelFutureListener() {//
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                assert  f == channelFuture;
                ctx.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
