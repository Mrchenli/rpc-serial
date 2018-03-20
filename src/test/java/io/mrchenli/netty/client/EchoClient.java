package io.mrchenli.netty.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class EchoClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("10.0.10.152",8080);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("hello echo server".getBytes());
        InputStream is = socket.getInputStream();
        byte[] b = new byte[1024];
        int flag;
        flag = is.read(b);
        System.out.println(new String(b));
    }

}
