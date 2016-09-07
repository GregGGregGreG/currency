package org.baddev.currency.ui.security.event;

import org.baddev.currency.core.event.BaseNotificationEvent;
import org.baddev.currency.ui.security.entity.LoginData;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public class LoginEvent extends BaseNotificationEvent<LoginData> {

    public LoginEvent(Object source, LoginData eventData) {
        super(source, eventData);
    }
}
