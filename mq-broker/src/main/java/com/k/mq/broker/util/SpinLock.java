package com.k.mq.broker.util;

import java.util.concurrent.atomic.AtomicInteger;

public class SpinLock implements PutMessageLock {
    private AtomicInteger locked = new AtomicInteger(0);

    @Override
    public void lock() {
        while (!locked.compareAndSet(0, 1)) {
            Thread.yield();
        }
    }

    @Override
    public void unlock() {
        locked.set(0);
    }
}
