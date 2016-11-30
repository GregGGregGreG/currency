package org.baddev.currency.ui.event.binder;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.ResetPasswordDTO;
import org.baddev.currency.ui.core.event.SafeProcessableBinderEvent;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
public final class ResetPwdStep2Event extends SafeProcessableBinderEvent<ResetPasswordDTO> {

    public ResetPwdStep2Event(UUID uuid, Object source, ResetPasswordDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<ResetPasswordDTO> successAction, FieldGroup binder) {
        super(uuid, source, eventData, failAction, successAction, binder);
    }

    public ResetPwdStep2Event(Object source, ResetPasswordDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<ResetPasswordDTO> successAction, FieldGroup binder) {
        super(source, eventData, failAction, successAction, binder);
    }
}
