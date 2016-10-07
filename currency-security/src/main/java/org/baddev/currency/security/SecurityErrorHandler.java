package org.baddev.currency.security;

import org.baddev.currency.core.CommonErrorHandler;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by IPotapchuk on 10/6/2016.
 */
public class SecurityErrorHandler extends CommonErrorHandler {

    @Override
    public boolean handle(Exception e) {
        boolean handled = super.handle(e);
        if (!handled) {
            if (e instanceof AuthenticationException) {
                log.info(e.getMessage(), e);
                return true;
            } else if (e instanceof SecurityException) {
                log.info(e.getMessage(), e);
            }
        }
        return handled;
    }
}
