package io.mrchenli.netty.reactor.reactorpattern;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class StateObjHandler implements Runnable {

    final SocketChannel socket;
    final SelectionKey sk;

    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);

    static final int READING = 0,SENDING=1;
    int state = READING;

    public StateObjHandler(Selector sel, SocketChannel c) throws IOException {
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
            socket.read(input);
            if(inputIsComplete()){
                process();
                sk.attach(new Sender());
                sk.interestOps(SelectionKey.OP_WRITE);
                sk.selector().wakeup();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Sender implements Runnable{

        @Override
        public void run() {
            try {
                socket.write(output);
                if(outputIsComplete()){
                    sk.channel();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
