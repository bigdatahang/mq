package com.k.mq.namesrv.event;

import com.google.common.collect.Lists;
import com.k.mq.common.util.ReflectionUtil;
import com.k.mq.namesrv.event.model.Event;
import com.k.mq.namesrv.event.spi.listener.Listener;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.*;

public class EventBus {
    private Map<Class<? extends Event>, List<Listener>> eventListenerMap = new ConcurrentHashMap<>();
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            10,
            100,
            3,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("event-bus-publish-task");
                    return thread;
                }
            }
    );

    public void init() {
        ServiceLoader<Listener> serviceLoader = ServiceLoader.load(Listener.class);
        for (Listener listener : serviceLoader) {
            Class clazz = ReflectionUtil.getInterfaceT(listener, 0);
            registry(clazz, listener);
        }
        System.out.println(eventListenerMap);
    }

    public <E extends Event> void registry(Class<? extends Event> clazz, Listener<E> listener) {
        List<Listener> listeners = eventListenerMap.get(clazz);
        if (CollectionUtils.isEmpty(listeners)) {
            eventListenerMap.put(clazz, Lists.newArrayList(listener));
        } else {
            listeners.add(listener);
            eventListenerMap.put(clazz, listeners);
        }
    }

    public void publish(Event event) {
        List<Listener> listeners = eventListenerMap.get(event.getClass());
        threadPoolExecutor.execute(() -> {
            for (Listener listener : listeners) {
                listener.onReceive(event);
            }
        });
    }

    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        eventBus.init();
    }
}
