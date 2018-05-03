package io.mrchenli.netty.reactor.classicserversocketloop;

import java.io.IOException;
import java.net.Socket;

public class Handler implements Runnable {

    final Socket socket;

    public Handler(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            byte[] input = new byte[1024];
            socket.getInputStream().read(input);
            byte[] output = process(input);
            socket.getOutputStream().write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] process(byte[] cmd){
        return cmd;
    }

}
