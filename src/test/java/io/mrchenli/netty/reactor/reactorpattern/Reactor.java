package io.mrchenli.netty.reactor.reactorpattern;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Channels :
 *      connections to files sockets etc that support non-blocking reads
 * Buffers
 *      Array-like objects that can be directly read or write by channels
 * Selectors
 *      tell which of a set of channels have io events
 * SelectionKeys
 *      Maintain IO event status and bindings
 */
public class Reactor implements Runnable {

    final Selector selector;
    final ServerSocketChannel serverSocket;

    public Reactor(int port) throws IOException {
        this.selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        SelectionKey sk = serverSocket.register(selector,SelectionKey.OP_ACCEPT);
        sk.attach(new Acceptor());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                selector.select();
                Set selected = selector.selectedKeys();
                Iterator it = selected.iterator();
                while (it.hasNext()){
                    dispatch((SelectionKey) it.next());
                }
                selected.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void dispatch(SelectionKey k){
        Runnable r = (Runnable) k.attachment();
        if(r!=null){
            r.run();//这个地方
        }
    }

    class Acceptor implements Runnable{

        @Override
        public void run() {
            try {
                SocketChannel c = serverSocket.accept();
                if(c!=null){
                    new Handler(selector,c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
