package com.k.mq.namesrv.event.listener;

import com.k.mq.namesrv.event.model.Event;

public interface Listener<E extends Event> {
    void onReceive(E event);
}
