package org.baddev.currency.core.event;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.baddev.currency.core.util.Safe.tryCall;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public class NotifierImpl implements Notifier {

    private Set<NotificationListener> listeners = new HashSet<>();

    @Override
    public void doNotify(NotificationEvent event) {
        tryCall(() -> listeners.forEach(l -> l.notificationReceived(event)));
    }

    @Override
    public boolean subscribe(NotificationListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean unsubscribe(NotificationListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public Collection<NotificationListener> getSubscribers() {
        return listeners;
    }
}
