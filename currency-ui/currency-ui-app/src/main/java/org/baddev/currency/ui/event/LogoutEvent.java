package org.baddev.currency.ui.event;

import org.baddev.common.event.BaseDataEvent;

import java.util.UUID;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public class LogoutEvent extends BaseDataEvent<String> {

    public LogoutEvent(UUID uuid, Object source, String userName) {
        super(uuid, source, userName);
    }

    public LogoutEvent(Object source, String eventData) {
        super(source, eventData);
    }
}
