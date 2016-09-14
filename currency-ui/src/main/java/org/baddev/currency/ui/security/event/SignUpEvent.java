package org.baddev.currency.ui.security.event;

import org.baddev.currency.core.event.BaseNotificationEvent;
import org.baddev.currency.security.dto.SignUpDTO;

/**
 * Created by IPotapchuk on 6/30/2016.
 */
public class SignUpEvent extends BaseNotificationEvent<SignUpDTO> {

    public SignUpEvent(Object source, SignUpDTO eventData) {
        super(source, eventData);
    }
}
