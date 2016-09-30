package org.baddev.currency.core.event;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public abstract class BaseNotificationEvent<T> implements NotificationEvent<T>{

    private Object source;
    private T eventData;

    public BaseNotificationEvent(Object source, T eventData) {
        this.source = source;
        this.eventData = eventData;
    }

    @Override
    public T getEventData() {
        return eventData;
    }

    @Override
    public Object getSource() {
        return source;
    }

}
