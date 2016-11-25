package org.baddev.currency.ui.event.binder;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.PasswordChangeDTO;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 11/15/2016.
 */
public class PasswordChangeEvent extends SafeProcessableBinderEvent<PasswordChangeDTO> {

    public PasswordChangeEvent(UUID uuid, Object source, PasswordChangeDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<PasswordChangeDTO> successAction, FieldGroup binder) {
        super(uuid, source, eventData, failAction, successAction, binder);
    }

    public PasswordChangeEvent(Object source, PasswordChangeDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<PasswordChangeDTO> successAction, FieldGroup binder) {
        super(source, eventData, failAction, successAction, binder);
    }
}
