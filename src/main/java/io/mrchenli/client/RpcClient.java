package io.mrchenli.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 如何关掉
 * 异步请求
 * 连接池
 * 服务发现
 */
public class RpcClient {

    private String host;
    private int port;
    private Bootstrap bootstrap;
    private EventLoopGroup worker;

    public RpcClient(String host, int port){
        try {
            this.host = host;
            this.port = port;
            worker = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.connect(host,port).sync().channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            worker.shutdownGracefully();
        }
    }

}
