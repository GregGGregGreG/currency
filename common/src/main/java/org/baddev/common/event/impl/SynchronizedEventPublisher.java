package org.baddev.common.event.impl;

import org.baddev.common.event.BaseDataEvent;
import org.baddev.common.event.EventPublisher;
import org.baddev.common.event.GenericEventListener;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public class SynchronizedEventPublisher implements EventPublisher {

    private final Logger log;

    private Set<GenericEventListener> listeners = Collections.synchronizedSet(new HashSet<>());

    public SynchronizedEventPublisher(Logger log) {
        this.log = log;
    }

    @Override
    public synchronized void publish(BaseDataEvent event) {
        log.debug("Event published to {} subscribers", listeners.size());
        listeners.forEach(l -> l.onEvent(event));
    }

    @Override
    public boolean subscribe(GenericEventListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean unsubscribe(GenericEventListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public Collection<GenericEventListener> getSubscribers() {
        return listeners;
    }
}
