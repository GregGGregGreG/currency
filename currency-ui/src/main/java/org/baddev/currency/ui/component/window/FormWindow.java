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

    public FormWindow(Class<T> beanClass, Map<String, String> captionToPropertyMap, Map<String, Class<? extends Field>> propertyToFieldTypeMap) {
        this(newBeanFieldGroup(beanClass, null, captionToPropertyMap, propertyToFieldTypeMap));
    }

    public FormWindow(Class<T> beanClass, T formBean, Map<String, String> captionToPropertyMap, Map<String, Class<? extends Field>> propertyToFieldTypeMap) {
        this(newBeanFieldGroup(beanClass, formBean, captionToPropertyMap, propertyToFieldTypeMap));
    }

    public FormWindow<T> withSubmitActionProvider(Consumer<BeanFieldGroup<T>> provider) {
        this.successActionProvider = provider;
        return this;
    }

    public FormWindow<T> withErrorActionProvider(Consumer<FieldGroup.CommitException> provider) {
        errorActionProvider = provider;
        return this;
    }

    public FormWindow<T> withReadOnlyFields(String... propIds) {
        for (String propId : propIds)
            binder.getField(propId).setReadOnly(true);
        return this;
    }

    public void configure(Consumer<BeanFieldGroup<T>> onCommitSuccess, Consumer<FieldGroup.CommitException> onCommitError) {
        configureBinder(onCommitSuccess);
        submitBtn = new Button();
        configureSubmitBtn(submitBtn, onCommitError);
        form = new FormLayout();
        form.setSpacing(true);
        form.setSizeUndefined();
    }

    private void configureBinder(Consumer<BeanFieldGroup<T>> onCommitSuccess) {
        if (mode == WindowMode.READONLY) binder.setReadOnly(true);
        else
            binder.addCommitHandler(new FieldGroup.CommitHandler() {
                @Override
                public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                }

                @Override
                public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                    onCommitSuccess.accept(binder);
                    close();
                    NotificationUtils.notifySuccess("Changes Submit", "Changes successfully saved");
                }
            });
    }

    private void configureSubmitBtn(Button submitBtn, Consumer<FieldGroup.CommitException> onCommitError) {
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

    private void show(WindowMode mode, String caption, Consumer<BeanFieldGroup<T>> onCommitSuccess, Consumer<FieldGroup.CommitException> onCommitError) {
        this.mode = mode;
        setCaption(caption);
        configure(onCommitSuccess, onCommitError);
        binder.getFields().forEach(f -> {
            if (f instanceof AbstractTextField) {
                f.setWidth(300, Unit.PIXELS);
                f.setRequired(true);
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
        show(WindowMode.EDIT, caption, onCommitSuccess, onCommitError);
    }

    public void showEdit(String caption, Consumer<BeanFieldGroup<T>> onCommitSuccess) {
        showEdit(caption, onCommitSuccess, errorActionProvider);
    }

    public void showEdit(String caption) {
        showEdit(caption, successActionProvider, errorActionProvider);
    }

    public void showNew(String caption, Consumer<BeanFieldGroup<T>> onCommitSuccess, Consumer<FieldGroup.CommitException> onCommitError) {
        binder.setItemDataSource((T) null);
        show(WindowMode.NEW, caption, onCommitSuccess, onCommitError);
    }

    public void showNew(String caption, Consumer<BeanFieldGroup<T>> onCommitSuccess) {
        showNew(caption, onCommitSuccess, errorActionProvider);
    }

    public void showNew(String caption) {
        showNew(caption, successActionProvider, errorActionProvider);
    }

    public void showReadOnly(String caption) {
        show(WindowMode.READONLY, caption, successActionProvider, errorActionProvider);
    }

    private static <T> BeanFieldGroup<T> newBeanFieldGroup(Class<T> beanClass, T formBean, Map<String, String> captionToPropertyMap, Map<String, Class<? extends Field>> propertyToFieldTypeMap) {
        BeanFieldGroup<T> bfg = new BeanFieldGroup<>(beanClass);
        bfg.setItemDataSource(formBean);
        captionToPropertyMap.entrySet().forEach(en -> {
            Class<? extends Field> fieldType = propertyToFieldTypeMap.get(en.getValue());
            bfg.buildAndBind(en.getKey(), en.getValue(), fieldType);
        });
        return bfg;
    }

    private static <T> BeanFieldGroup<T> newBeanFieldGroup(Class<T> beanClass, T formBean, Map<String, String> captionToPropertyMap) {
        BeanFieldGroup<T> bfg = new BeanFieldGroup<>(beanClass);
        bfg.setItemDataSource(formBean);
        captionToPropertyMap.entrySet().forEach(en -> bfg.buildAndBind(en.getKey(), en.getValue()));
        return bfg;
    }

}
