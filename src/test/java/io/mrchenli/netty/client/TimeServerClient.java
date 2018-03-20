package io.mrchenli.netty.client;

import io.mrchenli.netty.client.decoder.PojoTimeDecoder;
import io.mrchenli.netty.client.decoder.TimeDecoder;
import io.mrchenli.netty.client.handler.PojoTimerClientHandler;
import io.mrchenli.netty.client.handler.TimerClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 需要读懂channel future 这一块的异步 线程池
 */
public class TimeServerClient {

    public static void main(String[] args) throws InterruptedException {
        String ip="10.0.10.152";
        int port=8888;
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new PojoTimeDecoder(),new PojoTimerClientHandler());
                    }
                });
        //start the client  why sync ? cause connect is not sync so we have to wait really connect
        ChannelFuture f = bootstrap.connect(ip,port).sync();
        //wait until the connection is closed
        f.channel().closeFuture().sync();
    }

}
