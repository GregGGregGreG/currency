package org.baddev.currency.ui.security.event;

import org.baddev.currency.core.event.BaseNotificationEvent;
import org.baddev.currency.security.dto.LoginDTO;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public class LoginEvent extends BaseNotificationEvent<LoginDTO> {

    public LoginEvent(Object source, LoginDTO eventData) {
        super(source, eventData);
    }
}
