package com.k.mq.broker.util;

public interface PutMessageLock {
    void lock();

    void unlock();
}
