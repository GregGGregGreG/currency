package org.baddev.currency.ui.event.binder;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.UserDetailsDTO;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 11/17/2016.
 */
public class AccountDetailsChangeEvent extends SafeProcessableBinderEvent<UserDetailsDTO> {

    public AccountDetailsChangeEvent(UUID uuid, Object source, UserDetailsDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<UserDetailsDTO> successAction, FieldGroup binder) {
        super(uuid, source, eventData, failAction, successAction, binder);
    }

    public AccountDetailsChangeEvent(Object source, UserDetailsDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<UserDetailsDTO> successAction, FieldGroup binder) {
        super(source, eventData, failAction, successAction, binder);
    }
}
