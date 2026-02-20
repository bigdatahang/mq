package com.k.mq.broker.util;

import java.util.concurrent.locks.ReentrantLock;

public class UnfairReentrantLock implements PutMessageLock {
    private ReentrantLock reentrantLock = new ReentrantLock();

    @Override
    public void lock() {
        reentrantLock.lock();
    }

    @Override
    public void unlock() {
        reentrantLock.unlock();
    }
}
