package org.baddev.currency.core.event;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public interface NotificationEvent<T> {
    T getEventData();
    Object getSource();
}
