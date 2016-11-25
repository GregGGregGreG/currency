package org.baddev.currency.ui.event.binder;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.SignUpDTO;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 6/30/2016.
 */
public final class SignUpEvent extends SafeProcessableBinderEvent<SignUpDTO> {

    public SignUpEvent(UUID uuid, Object source, SignUpDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<SignUpDTO> successAction, FieldGroup binder) {
        super(uuid, source, eventData, failAction, successAction, binder);
    }

    public SignUpEvent(Object source, SignUpDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<SignUpDTO> successAction, FieldGroup binder) {
        super(source, eventData, failAction, successAction, binder);
    }
}
