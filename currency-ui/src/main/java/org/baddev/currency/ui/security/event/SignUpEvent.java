package org.baddev.currency.ui.security.event;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.SignUpDTO;

/**
 * Created by IPotapchuk on 6/30/2016.
 */
public final class SignUpEvent extends BinderEvent<SignUpDTO> {

    public SignUpEvent(Object source, SignUpDTO eventData, FieldGroup binder) {
        super(source, eventData, binder);
    }
}
