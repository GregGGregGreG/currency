package org.baddev.currency.ui.component.view.base;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.StringUtils;
import org.baddev.currency.ui.component.base.VerticalSpacedLayout;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public abstract class AbstractFormView<T> extends AbstractView implements FieldGroup.CommitHandler, Button.ClickListener {

    protected T formBean;
    private Class<T> beanClass;
    private String panelCaption = "";
    private BeanFieldGroup<T> binder;

    protected AbstractFormView(T formBean, Class<T> beanClass) {
        this.formBean = formBean;
        this.beanClass = beanClass;
    }

    protected void setPanelCaption(String caption){
        this.panelCaption = caption;
    }

    @Override
    protected final void init(VerticalSpacedLayout rootLayout) {
        FormLayout form = new FormLayout();
        form.setSizeUndefined();

        binder = new BeanFieldGroup<>(beanClass);
        binder.setItemDataSource(formBean);

        binder.addCommitHandler(this);

        Button submitBtn = new Button();
        submitBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        submitBtn.addClickListener(this);
        submitBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);

        customizeForm(form, binder, submitBtn);

        Label caption = new Label(panelCaption ,ContentMode.HTML);
        caption.addStyleName(ValoTheme.LABEL_COLORED);
        caption.addStyleName(ValoTheme.LABEL_BOLD);

        VerticalSpacedLayout wrapper = new VerticalSpacedLayout();
        if(!StringUtils.isEmpty(panelCaption)) wrapper.addComponent(caption);
        wrapper.addComponent(form);
        wrapper.setSizeUndefined();
        wrapper.setComponentAlignment(form, Alignment.MIDDLE_CENTER);

        Panel panel = new Panel(wrapper);
        panel.setStyleName(ValoTheme.PANEL_WELL);
        panel.setSizeUndefined();

        rootLayout.addComponent(panel);
        rootLayout.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

        submitBtn.focus();
    }

    @Override
    public final void buttonClick(Button.ClickEvent clickEvent) {
        submitBtnClicked(clickEvent, binder);
    }

    protected abstract void submitBtnClicked(Button.ClickEvent clickEvent, BeanFieldGroup<T> binder);

    protected abstract void customizeForm(final FormLayout formLayout, final BeanFieldGroup<T> binder, final Button submitBtn);

}
