package org.baddev.currency.ui.component.window.form;

import com.google.common.collect.Sets;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TwinColSelect;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by IPotapchuk on 10/4/2016.
 */
class TwincolSelectWindow<T> extends AbstractFormWindow<Set<T>, Exception> {

    private TwinColSelect twinColSelect;
    private Set<T> lhs;
    private Set<T> rhs;
    private Function<T, String> captionProducer;
    private boolean changed;

    TwincolSelectWindow(Collection<? extends T> lhs, Collection<? extends T> rhs, Function<T, String> captionProducer) {
        super(FormWindow.Mode.EDIT);
        this.lhs = Collections.unmodifiableSet(Sets.newLinkedHashSet(lhs));
        this.rhs = Collections.unmodifiableSet(Sets.newLinkedHashSet(rhs));
        this.captionProducer = captionProducer;
        submitBtn.setEnabled(false);
    }

    @Override
    protected Set<T> getEntity() {
        if(twinColSelect.isMultiSelect()){
            return (Set<T>) twinColSelect.getValue();
        } else {
            return new HashSet<>(Collections.singletonList((T) twinColSelect.getValue()));
        }
    }

    @Override
    protected boolean submit(Set<T> entity, Consumer<Set<T>> successActionProvider) throws Exception {
        if (changed) successActionProvider.accept(entity);
        return changed;
    }

    @Override
    protected void postInit(FormLayout form, Consumer<Set<T>> onCommitSuccess) {
        twinColSelect = new TwinColSelect();
        twinColSelect.setNullSelectionAllowed(true);
        twinColSelect.setMultiSelect(true);
        twinColSelect.setImmediate(true);
        twinColSelect.setLeftColumnCaption("Available");
        twinColSelect.setRightColumnCaption("Selected");
        twinColSelect.setRows(lhs.size());
        twinColSelect.addItems(lhs);
        twinColSelect.setValue(rhs);
        twinColSelect.getItemIds().forEach(id -> twinColSelect.setItemCaption(id, captionProducer.apply((T) id)));
        if (getMode() == FormWindow.Mode.READONLY) twinColSelect.setReadOnly(true);
        form.addComponent(twinColSelect);
        twinColSelect.addValueChangeListener((event -> {
            changed = !rhs.equals(twinColSelect.getValue());
            submitBtn.setEnabled(changed);
        }));
    }

}
