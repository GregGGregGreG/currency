package org.baddev.currency.ui;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.UI;
import org.baddev.currency.core.CoreErrorHandler;
import org.baddev.currency.ui.util.NotificationUtils;

/**
 * Created by IPotapchuk on 10/6/2016.
 */
public class UIErrorHandler extends CoreErrorHandler {

    @Override
    protected final boolean handleNext(Exception e) {
        boolean handled = super.handleNext(e);
        if (!handled) {
            if (e instanceof FieldGroup.CommitException || e instanceof Validator.InvalidValueException) {
                log.warn("Failed to submit form", e);
                setNotificationParams("Submit Error", "Some fields contain errors. Check them and try again", Severity.WARN);
                return true;
            }
        }
        return handled;
    }

    @Override
    protected void showNotification(String title, String msg, Severity severity) {
        if (UI.getCurrent() == null) return;
        switch (severity) {
            case WARN: UI.getCurrent().access(() -> NotificationUtils.notifyWarn(title, msg)); break;
            case ERROR: UI.getCurrent().access(() -> NotificationUtils.notifyFailure(title, msg)); break;
        }
    }

}
