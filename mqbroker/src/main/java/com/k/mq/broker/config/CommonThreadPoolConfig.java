package com.k.mq.broker.config;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class CommonThreadPoolConfig {
    public static ScheduledThreadPoolExecutor refreshThreadExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("refresh-thread");
            return thread;
        }
    });
}
