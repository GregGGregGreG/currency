package org.baddev.common.event;

import java.util.EventObject;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public abstract class BaseDataEvent<T> extends EventObject{

    private T eventData;

    public BaseDataEvent(Object source, T eventData) {
        super(source);
        this.eventData = eventData;
    }

    public T getEventData() {
        return eventData;
    }

}
