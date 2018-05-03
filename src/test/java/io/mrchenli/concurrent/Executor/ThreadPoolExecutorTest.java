package io.mrchenli.concurrent.Executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolExecutorTest {
    /**
     * 饱和策略
     * 当队列和线程池都满了，说明线程处于饱和状态 ，那么必须采取一种策略来处理提交的新任务。
     * 这个策略默认情况下是AbortPolicy,表示无法处理任务的时候抛出异常
     * 策略有：
     *  AbortPolicy
     *  CallerRunsPolicy
     *  DiscardOldestPolicy
     *  DiscardPolicy
     * @param args
     */
    public static void main(String[] args) {
        //corePoolSize maximumSize keepAliveTime
        //创建一个线程池
        AtomicInteger count = new AtomicInteger();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                10,
                20,
                10,
                TimeUnit.DAYS,
                new ArrayBlockingQueue<Runnable>(10),
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );

        for (int i=0;i<100;i++){
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());
                    count.getAndIncrement();
                }
            });
        }
        //属性下ExecutorService接口的方法
        threadPoolExecutor.shutdown();
        while (Thread.activeCount()>1){//这个地方只能再测试的时候用呀

        }
        System.out.println(count.get());
    }


}
