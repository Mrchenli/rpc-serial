package io.mrchenli.netty.client;

import java.io.IOException;
import java.net.Socket;

public class DiscardClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("10.0.10.152",8080);
        socket.getOutputStream().write("hello".getBytes());
    }

}
