package org.baddev.currency.ui.security.event;

import org.baddev.currency.notifier.event.BaseNotificationEvent;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public class LogoutEvent extends BaseNotificationEvent<Object> {

    public LogoutEvent(Object source) {
        super(source, null);
    }
}
