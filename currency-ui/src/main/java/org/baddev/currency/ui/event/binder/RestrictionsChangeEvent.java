package org.baddev.currency.ui.event.binder;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.RestrictionsDTO;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 11/18/2016.
 */
public class RestrictionsChangeEvent extends SafeProcessableBinderEvent<RestrictionsDTO> {

    public RestrictionsChangeEvent(UUID uuid, Object source, RestrictionsDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<RestrictionsDTO> successAction, FieldGroup binder) {
        super(uuid, source, eventData, failAction, successAction, binder);
    }

    public RestrictionsChangeEvent(Object source, RestrictionsDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<RestrictionsDTO> successAction, FieldGroup binder) {
        super(source, eventData, failAction, successAction, binder);
    }
}
