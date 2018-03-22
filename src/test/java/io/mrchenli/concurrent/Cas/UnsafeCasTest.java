package io.mrchenli.concurrent.Cas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class UnsafeCasTest {

    public static void main(String[] args) throws InterruptedException, NoSuchFieldException {
        int NUM_OF_THREADS = 5;
        int NUM_OF_INCREMENTS = 100000000;
        ExecutorService service = Executors.newFixedThreadPool(NUM_OF_THREADS);
        Counter counter;
        /**
         * 为什么跑synchronized 要比 cas还要快？
         * 当线程数小的时候cas是有优势的 但是当线程数大的时候Synchronized会比较好
         */
        //counter=new CasCounter();// new CounterClient();
        counter=new SyncCounter();// new CounterClient();
        //counter = new NoLockCounter();
        //counter= new AtomicCounter();//这个网上有人猜测说可能有jvm层优化
        //counter = new LockCounter();
        long before = System.currentTimeMillis();
        for (int i=0;i<NUM_OF_THREADS;i++){
            service.submit(new CounterClient(counter,NUM_OF_INCREMENTS));
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
        long after = System.currentTimeMillis();
        System.out.println("counter result: "+ counter.getCounter());
        System.out.println("Time passed in ms :"+(after-before));
    }

}
