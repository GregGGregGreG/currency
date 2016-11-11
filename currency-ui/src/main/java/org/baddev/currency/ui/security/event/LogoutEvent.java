package org.baddev.currency.ui.security.event;

import org.baddev.common.event.BaseDataEvent;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public class LogoutEvent extends BaseDataEvent<Object> {

    public LogoutEvent(Object source) {
        super(source, null);
    }
}
