package io.mrchenli.netty.reactor.classicserversocketloop;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 缺点:
 *  可拓展性不好
 *      1.负载增加
 *      2.资源的添加
 *      3.满足可用性和性能
 *          低谷、高峰、服务质量
 * Divide-and-conquer is usually the best approach 分而治之
 *  Divide processing into small tasks
 *      each task performs an action without blocking
 *  Execute each task when it is enabled
 *      Here ,an IO event usually servers as trigger
 * Basic mechanisms supported in java.nio
 *      Non-blocking reads and writes
 *      Dispatch tasks associated with sensed IO events
 * Endless variation possible
 *
 */
public class Server implements Runnable{

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(8080);
            while (!Thread.interrupted()){
                new Thread(new Handler(ss.accept())).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
