package org.baddev.currency.ui.security.event;

import org.baddev.currency.core.event.BaseNotificationEvent;

/**
 * Created by IPotapchuk on 9/26/2016.
 */
public class LoggedInEvent extends BaseNotificationEvent<Object>{

    public LoggedInEvent(Object source, Object eventData) {
        super(source, eventData);
    }

}
