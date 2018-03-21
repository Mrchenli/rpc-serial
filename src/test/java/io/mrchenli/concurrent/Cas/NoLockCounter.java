package io.mrchenli.concurrent.Cas;

public class NoLockCounter implements Counter {

    private long counter = 0;

    @Override
    public void increment() {
        counter++;
    }

    @Override
    public long getCounter() {
        return counter;
    }
}
