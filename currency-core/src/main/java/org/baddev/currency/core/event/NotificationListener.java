package org.baddev.currency.core.event;


import java.io.Serializable;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public interface NotificationListener<T extends NotificationEvent> extends Serializable{
    void notificationReceived(T e);
}
