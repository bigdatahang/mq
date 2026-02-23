package com.k.mq.namesrv.event;

import com.google.common.collect.Lists;
import com.k.mq.namesrv.event.listener.Listener;
import com.k.mq.namesrv.event.model.Event;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private Map<Class<? extends Event>, List<Listener>> eventListenerMap = new ConcurrentHashMap<>();

    public void init() {
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
        for (Listener listener : listeners) {
            listener.onReceive(event);
        }
    }
}
