package org.baddev.currency.ui.event.binder;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.SignInDTO;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public final class SignInEvent extends SafeProcessableBinderEvent<SignInDTO> {

    public SignInEvent(UUID uuid, Object source, SignInDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<SignInDTO> successAction, FieldGroup binder) {
        super(uuid, source, eventData, failAction, successAction, binder);
    }

    public SignInEvent(Object source, SignInDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<SignInDTO> successAction, FieldGroup binder) {
        super(source, eventData, failAction, successAction, binder);
    }
}
