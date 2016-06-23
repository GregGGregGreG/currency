package org.baddev.currency.notifier.listener;

import org.baddev.currency.notifier.event.NotificationEvent;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public interface NotificationListener {
    <T extends NotificationEvent> void onNotificationEventReceived(T e);
}
