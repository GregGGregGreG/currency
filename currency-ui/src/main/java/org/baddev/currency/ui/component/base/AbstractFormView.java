package org.baddev.currency.ui.component.base;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public abstract class AbstractFormView<T> extends AbstractCcyView {

    protected T formBean;
    private Class<T> beanClass;

    public AbstractFormView(Class<T> beanClass, T formBean) {
        this.beanClass = beanClass;
        this.formBean = formBean;
    }

    @Override
    protected VerticalLayout contentRoot() {
        VerticalLayout root = super.contentRoot();
        FormLayout form = new FormLayout();
        BeanFieldGroup<T> binder = new BeanFieldGroup<>(beanClass);
        binder.setBuffered(true);
        binder.setItemDataSource(formBean);
        customizeForm(form, binder);
        root.addComponent(form);
        root.setExpandRatio(form, 1.0f);
        root.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        return root;
    }

    protected abstract void customizeForm(FormLayout formLayout, BeanFieldGroup<T> binder);

}
