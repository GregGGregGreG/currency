package org.baddev.currency.core.event.impl;

import org.baddev.currency.core.event.BaseDataEvent;
import org.baddev.currency.core.event.EventPublisher;
import org.baddev.currency.core.event.GenericEventListener;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public class SynchronizedEventPublisher implements EventPublisher {

    private Set<GenericEventListener> listeners = Collections.synchronizedSet(new HashSet<>());

    @Override
    public synchronized void publish(BaseDataEvent event) {
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
