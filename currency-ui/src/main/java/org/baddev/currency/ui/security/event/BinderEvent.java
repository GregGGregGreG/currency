package org.baddev.currency.ui.security.event;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.common.event.BaseDataEvent;

/**
 * Created by IPotapchuk on 11/11/2016.
 */
public abstract class BinderEvent<T> extends BaseDataEvent<T> {

    private FieldGroup binder;

    public BinderEvent(Object source, T eventData, FieldGroup binder) {
        super(source, eventData);
        this.binder = binder;
    }

    public FieldGroup getBinder() {
        return binder;
    }
}
