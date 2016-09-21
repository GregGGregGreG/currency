package org.baddev.currency.ui.component.base;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.baddev.currency.ui.CurrencyUI;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by IPotapchuk on 9/19/2016.
 */
public abstract class AbstractFormWindow<T> extends Window implements InitializingBean {

    private final float             width;
    private final String            caption;
    private final Button            submitBtn;
    private final BeanFieldGroup<T> binder;
    private final FormLayout        form;
    private final WindowMode        mode;

    protected enum WindowMode {
        NEW, EDIT, READONLY
    }

    protected AbstractFormWindow(WindowMode mode, String caption, float width, Class<T> beanClass) {
        this.mode = mode;
        binder = new BeanFieldGroup<>(beanClass);
        this.width = width;
        this.caption = caption;
        submitBtn = new Button();
        form = new FormLayout();
    }

    protected AbstractFormWindow(WindowMode mode, String caption, Class<T> beanClass) {
        this(mode, caption, 600, beanClass);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setCaptionAsHtml(true);
        setCaption(caption);
        setWidth(width, Unit.PIXELS);
        setModal(true);
        setResizable(false);

        configureBinder(binder);
        configureSubmitBtn(submitBtn);

        form.setSpacing(true);
        form.setSizeUndefined();
        center();
    }

    private void configureBinder(BeanFieldGroup<T> binder) {
        if (mode == WindowMode.READONLY) binder.setReadOnly(true);
        else
            binder.addCommitHandler(new FieldGroup.CommitHandler() {
                @Override
                public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                    close();
                }

                @Override
                public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                    onCommitSuccess(binder);
                }
            });
    }

    private void configureSubmitBtn(Button submitBtn) {
        if (mode == WindowMode.NEW) {
            submitBtn.setStyleName("success");
            submitBtn.setCaption("Add");
            submitBtn.setIcon(FontAwesome.PLUS_CIRCLE);
        } else if (mode == WindowMode.EDIT) {
            submitBtn.setStyleName("primary");
            submitBtn.setCaption("Edit");
            submitBtn.setIcon(FontAwesome.EDIT);
        }
        if (mode != WindowMode.READONLY) {
            submitBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
            submitBtn.addClickListener(event -> {
                try {
                    if (binder.isModified()) binder.commit();
                    else close();
                } catch (FieldGroup.CommitException e) {
                    onCommitError(e);
                }
            });
        }
    }

    protected abstract void onCommitSuccess(BeanFieldGroup<T> binder);

    protected abstract void onCommitError(FieldGroup.CommitException e);

    public void show(T formBean) {
        binder.setItemDataSource(formBean);
        customizeForm(form, binder);
        if (mode != WindowMode.READONLY)
            form.addComponent(submitBtn);
        VerticalLayout root = new VerticalLayout(form);
        root.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        setContent(root);
        CurrencyUI.currencyUI().addWindow(this);
    }

    public void show(T formBean, String captionDetails) {
        setCaption(caption + " - <b>" + captionDetails + "</b>");
        show(formBean);
    }

    protected abstract void customizeForm(FormLayout form, BeanFieldGroup<T> binder);

    public static abstract class EditModeWindow<T> extends AbstractFormWindow<T> {

        public EditModeWindow(String caption, float width, Class<T> beanClass) {
            super(WindowMode.EDIT, caption, width, beanClass);
        }

        public EditModeWindow(String caption, Class<T> beanClass) {
            super(WindowMode.EDIT, caption, beanClass);
        }
    }

    public static abstract class AddModeWindow<T> extends AbstractFormWindow<T> {

        public AddModeWindow(String caption, float width, Class<T> beanClass) {
            super(WindowMode.NEW, caption, width, beanClass);
        }

        public AddModeWindow(String caption, Class<T> beanClass) {
            super(WindowMode.NEW, caption, beanClass);
        }
    }

    public static abstract class ReadonlyModeWindow<T> extends AbstractFormWindow<T> {

        public ReadonlyModeWindow(String caption, float width, Class<T> beanClass) {
            super(WindowMode.READONLY, caption, width, beanClass);
        }

        public ReadonlyModeWindow(String caption, Class<T> beanClass) {
            super(WindowMode.NEW, caption, beanClass);
        }
    }

}
