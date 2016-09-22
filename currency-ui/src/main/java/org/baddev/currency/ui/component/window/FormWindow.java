package org.baddev.currency.ui.component.window;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.ui.CurrencyUI;
import org.baddev.currency.ui.util.NotificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 9/19/2016.
 */
public class FormWindow<T> extends Window {

    private static final Logger log = LoggerFactory.getLogger(FormWindow.class);

    private Button submitBtn;
    private BeanFieldGroup<T> binder;
    private FormLayout form;
    private WindowMode mode;

    private Consumer<FieldGroup.CommitException> errorActionProvider = e -> {
        log.warn("Form submit error", e);
        NotificationUtils.notifyWarn("Submit Error", "Check the fields and try again");
    };

    private Consumer<BeanFieldGroup<T>> successActionProvider = binder -> {
        binder.clear();
        log.warn("Using default success actions. Please, specify your own successActionProvider");
    };

    private enum WindowMode {
        NEW, EDIT, READONLY
    }

    private FormWindow(BeanFieldGroup<T> binder) {
        this.binder = binder;
        setWidth(600, Unit.PIXELS);
        setCaptionAsHtml(true);
        setModal(true);
        setResizable(false);
        center();
    }

    public FormWindow(Class<T> beanClass, T formBean, Map<String, String> captionToPropertyMap) {
        this(newBeanFieldGroup(beanClass, formBean, captionToPropertyMap));
    }

    public FormWindow(Class<T> beanClass, Map<String, String> captionToPropertyMap) {
        this(newBeanFieldGroup(beanClass, null, captionToPropertyMap));
    }

    public FormWindow<T> withSubmitActionProvider(Consumer<BeanFieldGroup<T>> provider) {
        this.successActionProvider = provider;
        return this;
    }

    public FormWindow<T> withErrorActionProvider(Consumer<FieldGroup.CommitException> provider) {
        errorActionProvider = provider;
        return this;
    }

    public void configure(BeanFieldGroup<T> binder, Consumer<BeanFieldGroup<T>> onCommitSuccess, Consumer<FieldGroup.CommitException> onCommitError) {
        configureBinder(binder, onCommitSuccess);
        submitBtn = new Button();
        configureSubmitBtn(submitBtn, binder, onCommitError);
        form = new FormLayout();
        form.setSpacing(true);
        form.setSizeUndefined();
    }

    private void configureBinder(BeanFieldGroup<T> binder, Consumer<BeanFieldGroup<T>> onCommitSuccess) {
        if (mode == WindowMode.READONLY) binder.setReadOnly(true);
        else
            binder.addCommitHandler(new FieldGroup.CommitHandler() {
                @Override
                public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                    close();
                }

                @Override
                public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                    onCommitSuccess.accept(binder);
                    NotificationUtils.notifySuccess("Changes Submit", "Changes successfully saved");
                }
            });
    }

    private void configureSubmitBtn(Button submitBtn, BeanFieldGroup<T> binder, Consumer<FieldGroup.CommitException> onCommitError) {
        if (mode == WindowMode.NEW) {
            submitBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
            submitBtn.setCaption("Add");
            submitBtn.setIcon(FontAwesome.PLUS_CIRCLE);
        } else if (mode == WindowMode.EDIT) {
            submitBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
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
                    onCommitError.accept(e);
                }
            });
        }
    }

    private void show(WindowMode mode, BeanFieldGroup<T> binder, String caption, Consumer<BeanFieldGroup<T>> onCommitSuccess, Consumer<FieldGroup.CommitException> onCommitError) {
        this.mode = mode;
        setCaption(caption);
        configure(binder, onCommitSuccess, onCommitError);
        binder.getFields().forEach(f -> {
            f.setWidth(300, Unit.PIXELS);
            f.setRequired(true);
            if (f instanceof AbstractTextField) {
                ((AbstractTextField) f).setNullRepresentation("");
                ((AbstractTextField) f).setImmediate(true);
            }
        });
        form.addComponents(binder.getFields().toArray(new Field[binder.getFields().size()]));
        if (mode != WindowMode.READONLY) {
            form.addComponent(submitBtn);
        }
        VerticalLayout root = new VerticalLayout(form);
        root.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        setContent(root);
        CurrencyUI.currencyUI().addWindow(this);
    }

    public void showEdit(String caption, Consumer<BeanFieldGroup<T>> onCommitSuccess, Consumer<FieldGroup.CommitException> onCommitError) {
        show(WindowMode.EDIT, binder, caption, onCommitSuccess, onCommitError);
    }

    public void showEdit(String caption, Consumer<BeanFieldGroup<T>> onCommitSuccess) {
        showEdit(caption, onCommitSuccess, errorActionProvider);
    }

    public void showEdit(String caption) {
        showEdit(caption, successActionProvider, errorActionProvider);
    }

    public void showNew(String caption, Consumer<BeanFieldGroup<T>> onCommitSuccess, Consumer<FieldGroup.CommitException> onCommitError) {
        binder.setItemDataSource((T) null);
        show(WindowMode.NEW, binder, caption, onCommitSuccess, onCommitError);
    }

    public void showNew(String caption, Consumer<BeanFieldGroup<T>> onCommitSuccess) {
        showNew(caption, onCommitSuccess, errorActionProvider);
    }

    public void showNew(String caption) {
        showNew(caption, successActionProvider, errorActionProvider);
    }

    public void showReadOnly(String caption) {
        show(WindowMode.READONLY, binder, caption, successActionProvider, errorActionProvider);
    }

    private static <T> BeanFieldGroup<T> newBeanFieldGroup(Class<T> beanClass, T formBean, Map<String, String> captionToPropertyMap) {
        BeanFieldGroup<T> binder = new BeanFieldGroup<>(beanClass);
        binder.setItemDataSource(formBean);
        captionToPropertyMap.entrySet().forEach(en -> binder.buildAndBind(en.getKey(), en.getValue()));
        return binder;
    }

}
