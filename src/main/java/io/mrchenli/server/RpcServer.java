package io.mrchenli.server;

import io.mrchenli.protocol.RpcDecoder;
import io.mrchenli.protocol.RpcEncoder;
import io.mrchenli.protocol.RpcRequest;
import io.mrchenli.protocol.RpcResponse;
import io.mrchenli.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private String serverAddress;//服务自己地址
    private ServiceRegistry serviceRegistry;//服务注册器


    private Map<String,Object> handlerMap = new HashMap<>();// 用来存放 服务的map
    private static ThreadPoolExecutor threadPoolExecutor;//业务逻辑处理的线程池

    private EventLoopGroup boss;//accept 线程池
    private EventLoopGroup worker;//socket read send 线程池

    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }


    public static void submit(Runnable task){
        if(threadPoolExecutor == null){
            synchronized (RpcServer.class){//todo 双重校验
                if(threadPoolExecutor==null){
                    //线程池设置大小一般为[cpu+1,2*cpu]
                    threadPoolExecutor = new ThreadPoolExecutor(
                            16,
                            16,
                            600L,
                            TimeUnit.SECONDS,
                            new ArrayBlockingQueue<Runnable>(65536)
                    );
                }
            }
        }
        threadPoolExecutor.submit(task);
    }

    public RpcServer addService(String interfaceName,Object serviceBean){
        if(!handlerMap.containsKey(interfaceName)){
            logger.info("Loading service:{}",interfaceName);
            handlerMap.put(interfaceName,serviceBean);
        }
        return this;
    }

    public void start() throws InterruptedException {
        if(boss==null&&worker==null){
            boss = new NioEventLoopGroup();
            worker = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                   // .addLast(new LengthFieldBasedFrameDecoder())//todo
                                    .addLast(new RpcDecoder(RpcRequest.class))//字节流解码成对象
                                    .addLast(new RpcEncoder(RpcResponse.class))//对象编码成字节流
                                    .addLast(new RpcHandler(handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            ChannelFuture future = bootstrap.bind(host,port).sync();
            logger.info("Server started on port{}",port);
            if(serviceRegistry!=null){
                serviceRegistry.register(serverAddress);//todo no impl
            }
            future.channel().closeFuture().sync();
        }
    }

    public void stop(){
        if(boss!=null){
            boss.shutdownGracefully();
        }
        if(worker!=null){
            worker.shutdownGracefully();
        }
    }

}
