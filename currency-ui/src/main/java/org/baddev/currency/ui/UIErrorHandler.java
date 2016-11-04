package org.baddev.currency.ui;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.security.SecurityErrorHandler;
import org.baddev.currency.ui.exception.WrappedUIException;
import org.baddev.currency.ui.util.NotificationUtils;

/**
 * Created by IPotapchuk on 10/6/2016.
 */
public class UIErrorHandler extends SecurityErrorHandler {

    @Override
    protected boolean handleNext(Exception e) {
        boolean handled = super.handleNext(e);
        if (!handled) {
            if (e instanceof WrappedUIException) {
                if (e.getCause() instanceof FieldGroup.CommitException) {
                    log.warn("Failed to submit form", e.getMessage());
                    NotificationUtils.notifyWarn("Submit Error",
                            "Some fields contain errors. Check them and try again");
                } else {
                    log.error(e.getMessage(), e);
                    NotificationUtils.notifyFailure("Unexpected Error", e.getMessage());
                }
                return true;
            }
        }
        return handled;
    }

}
