package io.mrchenli.concurrent.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 这个异步任务有7个转态
 *  1.NEW = 0 刚刚被创建的转态
 *  2.COMPLETING = 1 这个任务被执行中的转态
 *  3.NORMAL = 2 这个任务正常完成的转态
 *  4.EXCEPTIONAL = 3 这个任务出现了异常
 *  5.CANCELLED = 4 这个任务被取消了
 *  6.INTERRUPTING = 5 这个任务被中断中
 *  7.INTERRUPTED = 6 这个任务被中断了
 *
 * next to==>CAS
 */
public class FutureTaskTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Callable<Integer> call = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("正在計算結果");
                Thread.sleep(3000);
                return 1;
            }
        };
        FutureTask<Integer> task = new FutureTask(call);
        Thread thread = new Thread(task);
        thread.start();

        Integer result = task.get();
        System.out.println("拿到的結果為"+result);
    }

}
