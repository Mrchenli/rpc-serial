package io.mrchenli.server;

import io.mrchenli.protocol.decoder.RpcDecoder;
import io.mrchenli.protocol.encoder.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc 里面的服务要用什么来做容器?key value 是什么?
 * 1.用netty 打通网络
 * 2.约定协议
 * 3.服务注册
 * 4.服务发现
 * 5.形成集群
 * 6.心跳检测
 */
public class RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private int port;
    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private ServerBootstrap serverBootstrap;

    public RpcServer(int port) {
        try {
            this.port = port;
            boss = new NioEventLoopGroup();
            worker = new NioEventLoopGroup();
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcDecoder());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            serverBootstrap.bind(port).sync().channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer(8080);
    }

}
