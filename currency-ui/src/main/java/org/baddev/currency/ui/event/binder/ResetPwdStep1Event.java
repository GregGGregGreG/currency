package org.baddev.currency.ui.event.binder;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.ResetPasswordRequestDTO;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
public final class ResetPwdStep1Event extends SafeProcessableBinderEvent<ResetPasswordRequestDTO> {

    public ResetPwdStep1Event(UUID uuid, Object source, ResetPasswordRequestDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<ResetPasswordRequestDTO> successAction, FieldGroup binder) {
        super(uuid, source, eventData, failAction, successAction, binder);
    }

    public ResetPwdStep1Event(Object source, ResetPasswordRequestDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<ResetPasswordRequestDTO> successAction, FieldGroup binder) {
        super(source, eventData, failAction, successAction, binder);
    }
}
