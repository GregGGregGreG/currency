package org.baddev.currency.ui.security.event;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.ResetPasswordRequestDTO;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
public final class ResetPwdStep1Event extends BinderEvent<ResetPasswordRequestDTO> {

    public ResetPwdStep1Event(Object source, ResetPasswordRequestDTO eventData, FieldGroup binder) {
        super(source, eventData, binder);
    }
}
