package com.k.mq.namesrv.event.spi.listener;

import com.k.mq.namesrv.event.model.Event;

public interface Listener<E extends Event> {
    void onReceive(E event) throws Exception;
}
