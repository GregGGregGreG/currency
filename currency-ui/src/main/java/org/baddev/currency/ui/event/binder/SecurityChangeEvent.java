package org.baddev.currency.ui.event.binder;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.SecuritySettingsDTO;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 11/15/2016.
 */
public class SecurityChangeEvent extends SafeProcessableBinderEvent<SecuritySettingsDTO> {

    public SecurityChangeEvent(UUID uuid, Object source, SecuritySettingsDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<SecuritySettingsDTO> successAction, FieldGroup binder) {
        super(uuid, source, eventData, failAction, successAction, binder);
    }

    public SecurityChangeEvent(Object source, SecuritySettingsDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<SecuritySettingsDTO> successAction, FieldGroup binder) {
        super(source, eventData, failAction, successAction, binder);
    }
}
