package org.baddev.currency.ui.core.event;

import com.vaadin.data.fieldgroup.FieldGroup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.baddev.common.event.SafeProcessableDataEvent;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 11/11/2016.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class SafeProcessableBinderEvent<T> extends SafeProcessableDataEvent<T, FieldGroup, SafeProcessableBinderEvent<T>> {

    @Getter
    private final FieldGroup binder;

    protected SafeProcessableBinderEvent(UUID uuid,
                                         Object source,
                                         T eventData,
                                         BiConsumer<FieldGroup, Exception> failAction,
                                         Consumer<T> successAction,
                                         FieldGroup binder) {
        super(uuid, source, eventData, failAction, successAction);
        this.binder = binder;
    }

    protected SafeProcessableBinderEvent(Object source,
                                         T eventData,
                                         BiConsumer<FieldGroup, Exception> failAction,
                                         Consumer<T> successAction,
                                         FieldGroup binder) {
        super(source, eventData, failAction, successAction);
        this.binder = binder;
    }

    @Override
    protected FieldGroup getFailActionArg() {
        return binder;
    }

}
