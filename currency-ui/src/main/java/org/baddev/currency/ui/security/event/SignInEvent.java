package org.baddev.currency.ui.security.event;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.SignInDTO;
import org.baddev.currency.core.event.BaseDataEvent;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public class SignInEvent extends BaseDataEvent<SignInDTO> {

    private FieldGroup binder;

    public SignInEvent(Object source, SignInDTO eventData) {
        super(source, eventData);
    }

    public SignInEvent(Object source, SignInDTO eventData, BeanFieldGroup binder) {
        super(source, eventData);
        this.binder = binder;
    }

    public FieldGroup getBinder() {
        return binder;
    }
}
