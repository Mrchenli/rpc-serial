package io.mrchenli.concurrent.Cas;

import sun.misc.Unsafe;

public class CasCounter implements Counter {

    private volatile long counter =0;

    private Unsafe unsafe;
    private long offset;

    public CasCounter() throws NoSuchFieldException {
        unsafe = UnsafeUtil.getUnsafe();
        offset = unsafe.objectFieldOffset(CasCounter.class.getDeclaredField("counter"));
    }

    @Override
    public void increment() {
        long before = counter;
        while (!unsafe.compareAndSwapLong(this,offset,before,before+1)){
            before = counter;//修改之前的值
        }
    }

    /**
     * get是不加锁的
     * @return
     */
    @Override
    public long getCounter() {
        return counter;
    }
}
