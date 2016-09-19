package org.baddev.currency.ui.component.base;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import org.baddev.currency.ui.CurrencyUI;
import org.springframework.beans.factory.InitializingBean;

import java.util.function.Supplier;

/**
 * Created by IPotapchuk on 9/19/2016.
 */
public abstract class AbstractLookupFormWindow<T> extends Window implements InitializingBean {

    private final float width;
    private final String caption;
    private Button submitBtn;
    private BeanFieldGroup<T> binder;
    private FormLayout form;

    protected AbstractLookupFormWindow(Class<T> beanClass, float width, String caption) {
        binder = new BeanFieldGroup<>(beanClass);
        this.width = width;
        this.caption = caption;
    }

    protected AbstractLookupFormWindow(Class<T> beanClass, String caption) {
       this(beanClass, 600, caption);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setWidth(width, Unit.PIXELS);
        setCaption(caption);
        setModal(true);
        setResizable(false);

        binder.addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
            }

            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                onCommitSuccess(binder);
            }
        });

        submitBtn = new Button();
        submitBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        submitBtn.addClickListener(event -> {
            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                onCommitError(e);
            }
        });

        form = new FormLayout();
        form.setSpacing(true);
        form.setSizeUndefined();
    }

    protected abstract void onCommitSuccess(BeanFieldGroup<T> binder);

    protected abstract void onCommitError(FieldGroup.CommitException e);

    public void show(Supplier<T> lookupOperation) {
        binder.setItemDataSource(lookupOperation.get());
        customizeForm(form, binder, submitBtn);
        VerticalLayout root = new VerticalLayout(form);
        root.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        setContent(root);
        center();
        CurrencyUI.currencyUI().addWindow(this);
    }

    protected abstract void customizeForm(FormLayout form, BeanFieldGroup<T> binder, Button submitBtn);
}
