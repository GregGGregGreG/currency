package org.baddev.currency.ui.security.event;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.SignUpDTO;
import org.baddev.currency.core.event.BaseNotificationEvent;

/**
 * Created by IPotapchuk on 6/30/2016.
 */
public class SignUpEvent extends BaseNotificationEvent<SignUpDTO> {

    private FieldGroup binder;

    public SignUpEvent(Object source, SignUpDTO eventData) {
        super(source, eventData);
    }

    public SignUpEvent(Object source, SignUpDTO eventData, FieldGroup binder) {
        super(source, eventData);
        this.binder = binder;
    }

    public FieldGroup getBinder() {
        return binder;
    }
}
