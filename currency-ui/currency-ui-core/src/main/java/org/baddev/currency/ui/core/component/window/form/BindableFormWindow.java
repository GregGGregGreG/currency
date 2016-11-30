package org.baddev.currency.ui.core.component.window.form;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 10/3/2016.
 */
class BindableFormWindow<T> extends AbstractFormWindow<Set<T>, FieldGroup.CommitException> {

    private BeanFieldGroup<T> binder;

    protected BindableFormWindow(FormWindow.Mode mode, BeanFieldGroup<T> binder) {
        super(mode);
        this.binder = binder;
    }

    BindableFormWindow<T> withReadOnlyFields(String... propIds) {
        for (String propId : propIds)
            binder.getField(propId).setReadOnly(true);
        return this;
    }

    @Override
    protected Set<T> getEntity() {
        return new HashSet<>(Collections.singletonList(binder.getItemDataSource().getBean()));
    }

    @Override
    protected boolean submit(Set<T> entity, Consumer<Set<T>> successActionProvider) throws FieldGroup.CommitException {
        boolean modified = binder.isModified();
        if (modified) binder.commit();
        return modified;
    }

    @Override
    protected void postInit(FormLayout form, Consumer<Set<T>> onCommitSuccess) {
        if (getMode() == FormWindow.Mode.READONLY) binder.setReadOnly(true);
        else
            binder.addCommitHandler(new FieldGroup.CommitHandler() {
                @Override
                public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                }

                @Override
                public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                    onCommitSuccess.accept(new HashSet<>(Collections.singletonList(binder.getItemDataSource().getBean())));
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
