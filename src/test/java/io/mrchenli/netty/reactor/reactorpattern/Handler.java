package io.mrchenli.netty.reactor.reactorpattern;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

final class Handler implements Runnable {

    final SocketChannel socket;
    final SelectionKey sk;

    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);

    static final int READING = 0,SENDING=1;
    int state = READING;

    public Handler(Selector sel, SocketChannel c) throws IOException {
        socket = c;
        c.configureBlocking(false);
        sk = socket.register(sel,0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        sel.wakeup();//why need this one?
    }

    boolean inputIsComplete(){return true;}
    boolean outputIsComplete(){return false;}

    void process(){}

    @Override
    public void run() {
        try {
            if(state==READING){
                read();
            }else if(state==SENDING){
                send();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    void read() throws IOException{
        socket.read(input);
        if(inputIsComplete()){
            process();
            state = SENDING;
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }



    void send() throws IOException {
        socket.write(output);
        if(outputIsComplete()){
            sk.channel();
        }
    }





}
