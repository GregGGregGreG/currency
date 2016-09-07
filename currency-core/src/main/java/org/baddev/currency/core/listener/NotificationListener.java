package org.baddev.currency.core.listener;


import org.baddev.currency.core.event.NotificationEvent;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public interface NotificationListener {
    <T extends NotificationEvent> void onNotificationEventReceived(T e);
}
