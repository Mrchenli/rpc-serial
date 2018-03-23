package io.mrchenli.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel serverChannel;
    /**
     * 调用的人可以是任何线程（这个会使得服务器自然的结束）
     */
    private volatile  boolean stop;//控制服务器的开关的

    /**
     * 构造器里面就把服务器启动了
     * @param port
     */
    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port),128);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port:"+port);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }


    //至少有开关功能把
    public void stop(){
        this.stop = true;
    }





    @Override
    public void run() {
        while (!stop){
            try {
                selector.select(1000);//设置个超时时间 以至于stop掉了还可以发现
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()){
                    key = it.next();
                    it.remove();
                    try {
                        //处理请求
                        handleInput(key);
                    }catch (Exception e){//如果key处理过程中出现了异常就把key取消掉
                        if(key!=null){
                            key.cancel();
                            if(key.channel() != null) key.channel().close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //如果服务器停了 就关闭多路复用器
        //关闭了多路复用器以后 所有注册在上面的channel和pipe等资源都会被自动释放
        if (selector!=null) {
            try {
                selector.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void  handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            if(key.isAcceptable()){
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector,SelectionKey.OP_READ);
            }

            if(key.isReadable()){
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if(readBytes>0){
                    readBuffer.flip();//limit = position position=0;
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println("The time server receive order:"+body);
                    String currentTime = "QUERY TIME ORDER"
                            .equalsIgnoreCase(body)?new Date().toString():"BAD ORDER";
                    //
                    doWrite(sc,currentTime);
                }else if(readBytes<0){
                    key.cancel();
                    sc.close();
                }
            }


        }
    }

    private void doWrite(SocketChannel channel,String response) throws IOException {
        if(response!=null&&response.trim().length()>0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }

}
