package io.mrchenli.netty.source;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutor;

import java.nio.channels.SelectionKey;

public class NioEventLoopTest {

    /**
     * 1.网络io操作
     * Channel read();//从当前的channel 中读取数据到第一个inbound缓冲区中
     *      ==>ChannelHandler.channelRead(ChannelHandlerContext,Object) 如果数据被成功读取
     *      ==>ChannelHandler.channelReadComplete(ChannelHandlerContext) 读取操作API 调用完成之后
     * ChannelFuture write(Object msg)//将当前的msg通过ChannelPipeline 写入到目标Channel中。 只有调用flush 才会写入到channel中
     *
     * ChannelFuture write(Object msg,ChannelPromise promise);//功能与write(Object msg)相同，但是携带了ChannelPromise 参数复杂设置写入操作的结果
     *
     * ChannelFuture writeAndFlush(Object msg,ChannelPromise promise);//
     *
     *ChannelFuture writeAndFlush(Object msg);
     *
     * Channel flush();
     *
     *ChannelFuture close(ChannelPromise promise);//主动关闭当前连接，通过ChannelPromise设置操作结果并进行结果通知
     *
     * ChannelFuture disconnect(ChannelPromise promise);
     *
     * ChannelFuture connect(SocketAddress remoteAddress);
     *
     * ChannelFuture connect(SocketAddress remoteAddress,SocketAddress localAddress);
     *
     * connect(SocketAddress remoteAddress,SocketAddress localAddress,ChannelPromise promise);
     *
     * ChannelFuture bind(SocketAddress localAddress);//绑定指定的本地Socket地址 localAddress
     *      ==>ChannelHandler.bind(ChannelHandlerContext,SocketAddress,ChannelPromise);
     *ChannelFuture bind(SocketAddress localAddress,ChannelPromise promise);
     *
     * ChannelConfig config();//获取当前Channel的配置信息，例如CONNECT_TIMEOUT_MILLS.
     *
     * boolean isOpen();//判断当前Channel是否已经注册到EventLoop上。
     *
     * boolean isRegistered();//判断当前Channel 是否已经注册到EventLoop上。
     *
     * boolean isActive();//判断当前Channel是否已经处于激活状态
     *
     * ChannelMetadata metadata();//获取当前Channel 的元数据描述信息，包括Tcp参数配置等。
     *
     * SocketAddress localAddress();//获取当前Channel的本地绑定地址
     *
     * SocketAddress remoteAddress();//获取当前Channel通信的远程Socket地址。
     */
    Channel channel;

    /**
     * 1.CLOSED_CHANNEL_EXCEPTION //链路已经关闭已经异常
     * 2.NOT_YET_CONNECTED_EXCEPTION //物理链路尚未建立异常
     *
     * estimatorHandler 用于预测下一个报文的大小
     *
     * parent: 代表父类的Channel;
     * id: 采用默认方式生成的全局唯一ID;
     * unsafe:Unsafe实例；
     * pipeline: 当前Channel对应的DefaultChannelPipeline;
     * eventLoop: 当前channel注册的EventLoop;
     *
     */
    AbstractChannel abstractChannel;

    DefaultChannelPipeline defaultChannelPipeline;

    SelectionKey selectionKey;

    NioSocketChannel nioSocketChannel;

    /**
     *
     */
    Channel.Unsafe unsafe;

    EventLoop eventExecutors;
    NioEventLoop loop;


    NioEventLoop eventLoop;

    NioEventLoopGroup group;

    ChannelPromise channelPromise;



}
