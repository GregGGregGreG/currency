package org.baddev.currency.ui.security.event;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.SignInDTO;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public final class SignInEvent extends BinderEvent<SignInDTO> {

    public SignInEvent(Object source, SignInDTO eventData, FieldGroup binder) {
        super(source, eventData, binder);
    }
}
