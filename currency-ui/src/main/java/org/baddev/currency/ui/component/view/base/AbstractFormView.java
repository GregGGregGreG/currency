package org.baddev.currency.ui.component.view.base;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public abstract class AbstractFormView<T> extends AbstractCcyView {

    protected T formBean;
    private Class<T> beanClass;
    private Button submitBtn;

    protected AbstractFormView(T formBean, Class<T> beanClass) {
        this.formBean = formBean;
        this.beanClass = beanClass;
    }

    @Override
    protected final void init(VerticalLayout rootLayout) {
        FormLayout form = new FormLayout();
        form.setSpacing(true);
        form.setMargin(new MarginInfo(true, true, true, false));
        form.setSizeUndefined();
        BeanFieldGroup<T> binder = new BeanFieldGroup<>(beanClass);
        binder.setBuffered(true);
        binder.setItemDataSource(formBean);
        submitBtn = new Button();
        submitBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        customizeForm(form, binder, submitBtn);
        rootLayout.addComponent(form);
//        rootLayout.setExpandRatio(form, 1.0f);
        rootLayout.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
    }

    protected abstract void customizeForm(final FormLayout formLayout, final BeanFieldGroup<T> binder, final Button submitBtn);

}
