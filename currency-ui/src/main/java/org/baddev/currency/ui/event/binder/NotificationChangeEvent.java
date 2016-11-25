package org.baddev.currency.ui.event.binder;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.NotificationSettingsDTO;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 11/14/2016.
 */
public class NotificationChangeEvent extends SafeProcessableBinderEvent<NotificationSettingsDTO> {

    public NotificationChangeEvent(UUID uuid, Object source, NotificationSettingsDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<NotificationSettingsDTO> successAction, FieldGroup binder) {
        super(uuid, source, eventData, failAction, successAction, binder);
    }

    public NotificationChangeEvent(Object source, NotificationSettingsDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<NotificationSettingsDTO> successAction, FieldGroup binder) {
        super(source, eventData, failAction, successAction, binder);
    }
}
