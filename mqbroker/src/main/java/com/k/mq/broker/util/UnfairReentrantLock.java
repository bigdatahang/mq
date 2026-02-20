package com.k.mq.broker.util;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 非公平可重入锁实现
 * 基于JDK的ReentrantLock实现PutMessageLock接口
 * 适用于需要可重入特性的场景
 * 
 * @author yihang07
 */
public class UnfairReentrantLock implements PutMessageLock {
    /** 内部使用的非公平可重入锁 */
    private ReentrantLock reentrantLock = new ReentrantLock();

    /**
     * 获取锁
     * 如果锁已被其他线程持有，当前线程将阻塞等待
     */
    @Override
    public void lock() {
        reentrantLock.lock();
    }

    /**
     * 释放锁
     * 必须由持有锁的线程调用
     */
    @Override
    public void unlock() {
        reentrantLock.unlock();
    }
}
