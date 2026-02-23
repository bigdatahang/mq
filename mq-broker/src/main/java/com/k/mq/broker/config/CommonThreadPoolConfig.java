package com.k.mq.broker.config;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * 通用线程池配置类
 * 提供全局共享的定时任务线程池
 *
 * @author yihang07
 */
public class CommonThreadPoolConfig {
    /**
     * 定时刷新线程池执行器
     * 用于执行刷新mq-topic.json文件
     */
    public static ScheduledThreadPoolExecutor refreshMQTopicThreadExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("refresh-mq-topic-thread");
            return thread;
        }
    });

    /**
     * 定时刷新线程池执行器
     * 用于执行刷新consumequeue-offset文件
     */
    public static ScheduledThreadPoolExecutor refreshConsumeQueueOffsetThreadExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("refresh-consume-queue-offset-thread");
            return thread;
        }
    });
}
