package org.baddev.currency.ui.security.event;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.event.BaseNotificationEvent;
import org.baddev.currency.security.dto.LoginDTO;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public class LoginEvent extends BaseNotificationEvent<LoginDTO> {

    private FieldGroup binder;

    public LoginEvent(Object source, LoginDTO eventData) {
        super(source, eventData);
    }

    public LoginEvent(Object source, LoginDTO eventData, BeanFieldGroup binder) {
        super(source, eventData);
        this.binder = binder;
    }

    public FieldGroup getBinder() {
        return binder;
    }
}
