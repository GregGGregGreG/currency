package org.baddev.currency.core.notifier;


import org.baddev.currency.core.event.NotificationEvent;
import org.baddev.currency.core.listener.NotificationListener;

import java.util.Collection;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public interface Notifier {
    <T extends NotificationEvent> void doNotify(T event);
    boolean subscribe(NotificationListener listener);
    boolean unsubscribe(NotificationListener listener);
    Collection<NotificationListener> getSubscribers();
}
