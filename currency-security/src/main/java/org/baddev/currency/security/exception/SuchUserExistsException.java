package org.baddev.currency.security.exception;

import org.baddev.currency.core.exception.ServiceException;

/**
 * Created by IPotapchuk on 6/30/2016.
 */
public class SuchUserExistsException extends ServiceException {

    private static final String MSG_PART = "User with username \"%s\" already exists";

    public SuchUserExistsException(String username) {
        super(String.format(MSG_PART, username));
    }

    public SuchUserExistsException(String username, Throwable cause) {
        super(String.format(MSG_PART, username), cause);
    }
}
