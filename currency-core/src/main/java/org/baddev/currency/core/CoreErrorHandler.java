package org.baddev.currency.core;

import org.baddev.common.CommonErrorHandler;
import org.baddev.currency.core.exception.ServiceException;
import org.jooq.exception.DataAccessException;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by IPotapchuk on 10/6/2016.
 */
public class CoreErrorHandler extends CommonErrorHandler {

    @Override
    protected boolean handleNext(Exception e) {
        if (e instanceof DataAccessException) {
            log.error("Error accessing data", e);
            setNotificationParams("System Error", "System Error Occurred", Severity.ERROR);
            return true;
        } else if (e instanceof ServiceException) {
            log.error("Service Error", e);
            setNotificationParams("Service Error", e.getMessage(), Severity.ERROR);
            return true;
        } else if (e instanceof AuthenticationException) {
            log.warn("Failed to authenticate", e);
            setNotificationParams("Authentication Error", e.getMessage(), Severity.ERROR);
            return true;
        }
        return false;
    }

}
