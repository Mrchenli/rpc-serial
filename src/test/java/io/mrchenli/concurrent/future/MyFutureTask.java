package io.mrchenli.concurrent.future;

import io.mrchenli.concurrent.Cas.UnsafeUtil;
import sun.misc.Unsafe;

import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;


public class MyFutureTask<V> implements Future<V>,Runnable {

    /**
     * 1.这个任务的转态是
     * 2.这个任务的执行行为（这个是放到线程中去执行的）
     * 3.这个任务的执行结果
     */
    private volatile int state;//1.转态

    private static final int NEW=0;//新建
    private static final int COMPLETING=1;//完成中
    private static final int CANCELLED=2;//取消
    private static final int INTERRUPTING=3;//中断中
    private static final int INTERRUPTED=4;//中断
    private static final int NORMAL=5;//正常结束
    private static final int EXCEPTION=6;//这个任务出异常了

    private Callable<V> callable;//2.行为部分

    //TODO
    private Object outcome;//3.结果(可见性问题 如果没有用多个线程来操作这个outcome就只要可见性了)

    private static final Unsafe UNSAFE;

    private volatile Thread runner;//todo 这个有啥用 记录是谁在跑这个任务（可以中断）
    private static final long runnerOffset;

    private static final long stateOffset;

    //等待在这个任务上面的线程们
    private WaitNode waiters;

    private static final long waitersOffset;



    static {
        Class clzz = MyFutureTask.class;
        try {
            UNSAFE = UnsafeUtil.getUnsafe();
            runnerOffset = UNSAFE.objectFieldOffset(clzz.getDeclaredField("runner"));
            stateOffset = UNSAFE.objectFieldOffset(clzz.getDeclaredField("state"));
            waitersOffset = UNSAFE.objectFieldOffset(clzz.getDeclaredField("waiters"));
        }catch (Exception e){
            throw new Error(e);
        }
    }


    public MyFutureTask(Callable<V> callable) {
        if(callable==null) throw new NullPointerException();
        this.callable = callable;
        this.state = NEW;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        int s = state;
        if(s==NEW||s==COMPLETING){
            s = awaitDone(false,0L);
        }
        return report(s);
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (unit == null)
            throw new NullPointerException();
        int s = state;
        if (s <= COMPLETING &&
                (s = awaitDone(true, unit.toNanos(timeout))) <= COMPLETING)
            throw new TimeoutException();
        return report(s);
    }


    @Override
    public void run() {
        if(state!=NEW||!UNSAFE.compareAndSwapObject(this,runnerOffset,null,Thread.currentThread())){
            return;//把当前线程记录下来
        }
        try {
            Callable<V> ca = this.callable;
            if(ca!=null&&state==NEW){
                V result;
                boolean ran;
                try {
                    result = ca.call();
                    ran = true;
                } catch (Throwable ex) {//如果任务执行过程中出现了异常
                    result = null;
                    ran = false;
                    setException(ex);
                }
                if (ran) set(result);
            }
        }finally {
            runner = null;
            // state must be re-read after nulling runner to prevent
            // leaked interrupts
            int s = state;
            //如何才能中断这个任务呢 todo 这里就需要拿到执行任务的那个线程了吧 来控制中断与否
            if (s == INTERRUPTING||s==INTERRUPTED)//如果转态是中断 或者中断中的转态
                handlePossibleCancellationInterrupt(s);
        }
    }

    public void handlePossibleCancellationInterrupt(int state){
        if(state==INTERRUPTING){
            while (state==INTERRUPTING) Thread.yield();
        }
    }

    public void setException(Throwable t){
        if(UNSAFE.compareAndSwapObject(this,stateOffset,NEW,COMPLETING)){
            outcome = t;
            UNSAFE.putOrderedInt(this,stateOffset,EXCEPTION);
            finishCompletion();
        }
    }

    protected void set(V v){
        //注意这里不是for 而是if 如果state不是NEW 而是被cancel了 或者被中断了 或者中断中呢
        if(UNSAFE.compareAndSwapInt(this,stateOffset,NEW,COMPLETING)){
            outcome = v;
            UNSAFE.putOrderedInt(this,stateOffset,NORMAL);
            finishCompletion();
        }
    }

    /**
     * Removes and signals all waiting threads
     */
    public void finishCompletion(){
        for (WaitNode q;(q=waiters)!=null;){//这里一定要用循环
            if(UNSAFE.compareAndSwapObject(this,waitersOffset,waiters,null)){
                for (;;){
                    Thread t = q.thread;
                    if(t!=null){
                        q.thread  = null;
                        LockSupport.unpark(t);//唤醒
                    }
                    WaitNode next = q.next;
                    if(next == null){
                        break;
                    }
                    q.next = null;//unlink to help gc
                    q = next;
                }
                break;
            }
        }

        done();
        callable = null;
    }

    public void done(){}



    public int awaitDone(boolean timed,long nanos) throws InterruptedException {
        final long deadline = timed?System.nanoTime()+nanos:0L;
        WaitNode q = null;
        boolean queued = false;
        for (;;){
            if(Thread.interrupted()){
                removeWaiter(q);
                throw new InterruptedException();
            }
            int s = state;
            if(s>COMPLETING){
                if(q!=null){
                    q.thread = null;
                }
                return s;
            }else if(s==COMPLETING){
                Thread.yield();
            }else if(q==null){
                q = new WaitNode();
            }else if(!queued){
                queued = UNSAFE.compareAndSwapObject(this,waitersOffset,q.next=waiters,q);
            }else if(timed){
                nanos = deadline - System.nanoTime();
                if(nanos<=0L){
                    removeWaiter(q);
                    return state;
                }
                LockSupport.parkNanos(this,nanos);
            }else{
                LockSupport.park(this);
            }

        }
    }

    public void removeWaiter(WaitNode node){
        if (node != null) {
            node.thread = null;
            retry:
            for (;;) {          // restart on removeWaiter race
                for (WaitNode pred = null, q = waiters, s; q != null; q = s) {
                    s = q.next;
                    if (q.thread != null)
                        pred = q;
                    else if (pred != null) {
                        pred.next = s;
                        if (pred.thread == null) // check for race
                            continue retry;
                    }
                    else if (!UNSAFE.compareAndSwapObject(this, waitersOffset,
                            q, s))
                        continue retry;
                }
                break;
            }
        }
    }

    public V report(int s) throws ExecutionException {
        Object x = outcome;
        if(state == NORMAL){
            return (V) x;
        }
        if(s==CANCELLED||s==INTERRUPTED||s==INTERRUPTING){
            throw new CancellationException();
        }
        throw new ExecutionException((Throwable) x);
    }


    static final class WaitNode{
        //private Thread thread;
        volatile Thread thread;
        //private WaitNode next;
        volatile WaitNode next;
        public WaitNode() {
            this.thread = Thread.currentThread();
        }
    }



}
