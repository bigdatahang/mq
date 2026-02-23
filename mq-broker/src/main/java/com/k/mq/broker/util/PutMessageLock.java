package com.k.mq.broker.util;

/**
 * 写入消息锁接口
 * 定义消息写入时需要的加锁和解锁操作
 * 
 * @author yihang07
 */
public interface PutMessageLock {
    /**
     * 获取锁
     */
    void lock();

    /**
     * 释放锁
     */
    void unlock();
}
