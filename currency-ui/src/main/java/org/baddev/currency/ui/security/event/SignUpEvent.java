package org.baddev.currency.ui.security.event;

import org.baddev.currency.core.event.BaseNotificationEvent;
import org.baddev.currency.ui.security.entity.SignUpData;

/**
 * Created by IPotapchuk on 6/30/2016.
 */
public class SignUpEvent extends BaseNotificationEvent<SignUpData> {

    public SignUpEvent(Object source, SignUpData eventData) {
        super(source, eventData);
    }
}
