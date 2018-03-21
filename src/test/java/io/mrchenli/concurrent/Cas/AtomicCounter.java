package io.mrchenli.concurrent.Cas;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter implements Counter{

    AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void increment() {
        counter.incrementAndGet();
    }

    @Override
    public long getCounter() {
        return counter.get();
    }


}
