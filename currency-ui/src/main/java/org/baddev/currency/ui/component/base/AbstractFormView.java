package org.baddev.currency.ui.component.base;

import com.google.common.eventbus.EventBus;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;
import org.baddev.currency.ui.component.window.SettingsWindow;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public abstract class AbstractFormView<T> extends AbstractCcyView {

    protected T formBean;
    private Class<T> beanClass;
    private Button submitBtn;

    protected AbstractFormView(SettingsWindow settingsWindow, EventBus bus, T formBean, Class<T> beanClass) {
        super(settingsWindow, bus);
        this.formBean = formBean;
        this.beanClass = beanClass;
    }

    @Override
    protected VerticalLayout contentRoot() {
        VerticalLayout root = super.contentRoot();
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
        root.addComponent(form);
        root.setExpandRatio(form, 1.0f);
        root.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        return root;
    }

    protected abstract void customizeForm(final FormLayout formLayout, final BeanFieldGroup<T> binder, final Button submitBtn);

}
