package org.baddev.currency.ui;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.security.SecurityErrorHandler;
import org.baddev.currency.ui.exception.WrappedUIException;
import org.baddev.currency.ui.util.NotificationUtils;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by IPotapchuk on 10/6/2016.
 */
public class UIErrorHandler extends SecurityErrorHandler {

    @Override
    public boolean handle(Exception e) {
        boolean handled = super.handle(e);
        if (!handled) {
            if (e instanceof WrappedUIException) {
                if (e.getCause() instanceof FieldGroup.CommitException) {
                    log.warn(e.getMessage(), e);
                    NotificationUtils.notifyWarn("Submit Error",
                            "Some fields contain errors. Check them and try again");
                } else {
                    log.error(e.getMessage(), e);
                    NotificationUtils.notifyFailure("Unexpected Error", e.getMessage());
                }
                return true;
            }
        }
        postHandle(e);
        return handled;
    }

    protected void postHandle(Exception e) {
        if (e instanceof ServiceException) {
            NotificationUtils.notifyFailure("Service Error", e.getMessage());
        } else if (e instanceof AuthenticationException) {
            NotificationUtils.notifyFailure("Login Error", e.getMessage());
        }
    }

}
