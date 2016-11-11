package org.baddev.currency.ui.security.event;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.ResetPasswordDTO;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
public final class ResetPwdStep2Event extends BinderEvent<ResetPasswordDTO> {

    public ResetPwdStep2Event(Object source, ResetPasswordDTO eventData, FieldGroup binder) {
        super(source, eventData, binder);
    }
}
