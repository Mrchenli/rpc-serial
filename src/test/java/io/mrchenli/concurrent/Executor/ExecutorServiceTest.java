package io.mrchenli.concurrent.Executor;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自己实现下就知道要实现哪些方法了
 * 看下AbstractExecutorService怎么实现的呀
 *
 * 所以第一步是干嘛呢？
 * execute方法 execute 得有个工作队列
 *
 * 1.工作队列(task)
 * 2.线程队列（worker）corePoolSize maxSize
 * 3.线程池的转态
 */
public class ExecutorServiceTest implements ExecutorService{

    /**
     *  runState:
     *      function:provides the main lifecycle control
     *      values:
     *          RUNNING,accept new tasks and process queued task
     *          SHUTDOWN,do'nt accept new task but process queued task
     *          STOP,do'nt accept ,do'nt process queued,interrupt in-process task
     *          TIDYING,all task terminated and workCount zero
     *          TERMINATED completed
     *  workerCount:
     *      function:indicating the effective number of threads
     * @param args
     */
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));

    //operation related to ctl
    private static final int COUNT_BITS = Integer.SIZE - 3;//29位
    //2的29次方-1
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;

    // Packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }

    //worker 的队列 核心大小 最大大小 空闲存活时间
    //private final BlockingQueue<Runnable> workQueue;//构造函数传进来的进行初始化的
    private final HashSet<Worker> workers = new HashSet<Worker>();
    //核心线程池大小corePoolSize
    private volatile int corePoolSize;
    private volatile int maximumPoolSize;
    private volatile long keepAliveTime;//当运行线程大于corePoolSize的时候 空闲线程的存活时间 到点就给回收了

    //用来创建线程的
    private volatile ThreadFactory threadFactory;

    //任务队列 队列满了得有抛弃策略把
    private BlockingQueue<Runnable> workQueue;


    //0.线程池的创建

    public ExecutorServiceTest(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> workQueue) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.workQueue = workQueue;
        threadFactory = Executors.defaultThreadFactory();
    }


    //1.对worker的操作
    private boolean addWorker(Runnable firstTask,boolean core){
        return false;
    }

    //2.对工作队列的操作

    //抛弃策略



    final void runWorker(Worker w){
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock();//allow interrupt
        boolean completeAbruptly = true;
        try {
            /*while (task!=null||){//todo

            }*/
        }finally {

        }

    }

    @Override
    public void execute(Runnable command) {

    }

    private final class Worker extends AbstractQueuedSynchronizer implements Runnable{

        final Thread thread;
        Runnable firstTask;
        volatile long completedTasks;

        public Worker(Runnable firstTask) {
            setState(-1);
            this.firstTask = firstTask;
            this.thread = threadFactory.newThread(this);
        }

        @Override
        public void run() {
            runWorker(this);
        }


        //todo 1.这个作用是什么呀
        public void unlock(){
            release(1);
        }
    }



    public static void main(String[] args) {
        AbstractExecutorService a = null;
        ThreadPoolExecutor t = null;
    }

    /**
     *
     */
    @Override
    public void shutdown() {

    }

    /**
     *
     * @return
     */
    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return null;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return null;
    }

    @Override
    public Future<?> submit(Runnable task) {
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    /**
     * 首先第一个是execute方法
     * @param command
     */
   /* @Override
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        //处理有三部
        //1.如果运行的线程数少于核心线程池大小，就开启一个新的线程来跑新的任务
        //2.如果可以从队列拿到一个任务 我们要双重检测（应为上次检测后线程死掉了或者线程池关闭了）
        //3.如果我们不能从队列消费，我们加试图加一个线程，如果失败我们会shutdown 或者拒绝这个任务
        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) {//如果比核心线程池小的话
            if (addWorker(command, true))//添加一个线程
                return;
            c = ctl.get();
        }
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            if (! isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        else if (!addWorker(command, false))
            reject(command);

    }*/
}
