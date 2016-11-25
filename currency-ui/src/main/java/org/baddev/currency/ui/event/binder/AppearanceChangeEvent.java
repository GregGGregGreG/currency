package org.baddev.currency.ui.event.binder;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.dto.AppearanceSettingsDTO;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 11/14/2016.
 */
public class AppearanceChangeEvent extends SafeProcessableBinderEvent<AppearanceSettingsDTO> {

    public AppearanceChangeEvent(UUID uuid, Object source, AppearanceSettingsDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<AppearanceSettingsDTO> successAction, FieldGroup binder) {
        super(uuid, source, eventData, failAction, successAction, binder);
    }

    public AppearanceChangeEvent(Object source, AppearanceSettingsDTO eventData, BiConsumer<FieldGroup, Exception> failAction, Consumer<AppearanceSettingsDTO> successAction, FieldGroup binder) {
        super(source, eventData, failAction, successAction, binder);
    }
}
