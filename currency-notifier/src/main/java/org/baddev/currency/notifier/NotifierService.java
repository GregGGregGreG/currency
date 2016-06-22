package org.baddev.currency.notifier;

import org.baddev.currency.notifier.event.NotificationEvent;
import org.baddev.currency.notifier.listener.NotificationListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public class NotifierService implements Notifier {

    private Set<NotificationListener> listeners = new HashSet<>();

    @Override
    public <T extends NotificationEvent> void doNotify(T event) {
        listeners.forEach(l -> l.onNotificationReceived(event));
    }

    @Override
    public boolean subscribe(NotificationListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean unsubscribe(NotificationListener listener) {
        return listeners.remove(listener);
    }
}
