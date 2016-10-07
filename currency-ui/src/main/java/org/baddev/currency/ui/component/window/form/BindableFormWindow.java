package org.baddev.currency.ui.component.window.form;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;

import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 10/3/2016.
 */
class BindableFormWindow<T extends FieldGroup> extends AbstractFormWindow<T, FieldGroup.CommitException> {

    private T binder;

    protected BindableFormWindow(FormWindow.Mode mode, T binder) {
        super(mode);
        this.binder = binder;
    }

    BindableFormWindow<T> withReadOnlyFields(String... propIds) {
        for (String propId : propIds)
            binder.getField(propId).setReadOnly(true);
        return this;
    }

    @Override
    protected T getEntity() {
        return binder;
    }

    @Override
    protected boolean submit(T entity, Consumer<T> successActionProvider) throws FieldGroup.CommitException {
        boolean notifyAfter = binder.isModified();
        if (notifyAfter) binder.commit();
        return notifyAfter;
    }

    @Override
    protected void postInit(FormLayout form, Consumer<T> successActionProvider) {
        if (getMode() == FormWindow.Mode.READONLY) binder.setReadOnly(true);
        else
            binder.addCommitHandler(new FieldGroup.CommitHandler() {
                @Override
                public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                }

                @Override
                public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                    successActionProvider.accept(binder);
                }
            });
        binder.getFields().forEach(f -> {
            if (f instanceof AbstractTextField) {
                f.setWidth(300, Unit.PIXELS);
                f.setRequired(true);
                ((AbstractTextField) f).setNullRepresentation("");
                ((AbstractTextField) f).setImmediate(true);
            }
        });
        form.addComponents(binder.getFields().toArray(new Field[binder.getFields().size()]));
    }

}
