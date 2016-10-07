package org.baddev.currency.ui.component.window.form;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.ui.CurrencyUI;
import org.baddev.currency.ui.exception.WrappedUIException;
import org.baddev.currency.ui.util.NotificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 10/3/2016.
 */
abstract class AbstractFormWindow<T, E extends Exception> extends Window {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private FormWindow.Mode mode;
    private FormLayout form;
    protected Button submitBtn;

    protected Consumer<E> errorActionProvider = e -> {
        log.warn("Form submit error", e);
        NotificationUtils.notifyWarn("Submit Error", "Check the fields and try again");
    };

    private Consumer<T> successActionProvider = e -> log.warn("Using default success actions. Please, specify your own successActionProvider");
    private boolean uiErrorHandlingMode = true;

    protected AbstractFormWindow(FormWindow.Mode mode) {
        this.mode = mode;
        setCaptionAsHtml(true);
        initForm();
        initSubmitBtn();
        center();
    }

    private void initSubmitBtn() {
        submitBtn = new Button();
        if (mode == FormWindow.Mode.NEW) {
            submitBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
            submitBtn.setCaption("Add");
            submitBtn.setIcon(FontAwesome.PLUS_CIRCLE);
        } else if (mode == FormWindow.Mode.EDIT) {
            submitBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
            submitBtn.setCaption("Edit");
            submitBtn.setIcon(FontAwesome.EDIT);
        }

        if (mode != FormWindow.Mode.READONLY) {
            submitBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
            submitBtn.addClickListener(event -> {
                try {
                    boolean notifyAfter = submit(getEntity(), successActionProvider);
                    close();
                    if (notifyAfter) NotificationUtils.notifySuccess("Changes Submit", "Changes successfully saved");
                } catch (Exception e) {
                    if (!uiErrorHandlingMode) errorActionProvider.accept((E) e);
                    else throw new WrappedUIException(e);
                }
            });
        }
    }

    protected boolean submit(T entity, Consumer<T> successActionProvider) throws E {
        successActionProvider.accept(entity);
        return true;
    }

    protected abstract T getEntity();

    private void initForm() {
        form = new FormLayout();
        form.setSpacing(true);
        form.setMargin(true);
        form.setSizeUndefined();
    }

    final FormWindow.Mode getMode() {
        return mode;
    }

    final void withUIErrorHandling(boolean uiErrorHandlingMode) {
        this.uiErrorHandlingMode = uiErrorHandlingMode;
    }

    final AbstractFormWindow<T, E> withWidth(float width) {
        setWidth(width, Unit.PIXELS);
        return this;
    }

    final AbstractFormWindow<T, E> withHeight(float height) {
        setHeight(height, Unit.PIXELS);
        return this;
    }

    final AbstractFormWindow<T, E> withErrorActionProvider(Consumer<E> provider) {
        errorActionProvider = provider;
        return this;
    }

    final AbstractFormWindow<T, E> withSuccessActionProvider(Consumer<T> provider) {
        this.successActionProvider = provider;
        return this;
    }

    final AbstractFormWindow<T, E> withCaption(String caption) {
        setCaption(caption);
        return this;
    }

    final void show(String caption, Consumer<T> onCommitSuccess, Consumer<E> onCommitError) {
        Objects.requireNonNull(onCommitSuccess);
        Objects.requireNonNull(onCommitError);
        Objects.requireNonNull(caption);
        setCaption(caption);
        postInit(form, onCommitSuccess);
        if (mode != FormWindow.Mode.READONLY)
            form.addComponent(submitBtn);
        VerticalLayout root = new VerticalLayout(form);
        root.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        setContent(root);
        CurrencyUI.get().addWindow(this);
    }

    protected abstract void postInit(FormLayout form, Consumer<T> onCommitSuccess);

    final void show(String caption) {
        show(caption, successActionProvider, errorActionProvider);
    }

}
