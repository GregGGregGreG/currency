package org.baddev.currency.core.event;


import java.io.Serializable;
import java.util.EventListener;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public interface GenericEventListener<T extends BaseDataEvent> extends EventListener, Serializable {
    void onEvent(T e);
}
