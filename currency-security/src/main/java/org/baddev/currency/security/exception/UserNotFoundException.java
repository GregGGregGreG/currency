package org.baddev.currency.security.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Created by IPotapchuk on 9/19/2016.
 */
public class UserNotFoundException extends NestedRuntimeException {

    private static final String MSG_PART = "User %s was not found";

    public UserNotFoundException(String userName) {
        super(String.format(MSG_PART, userName));
    }

    public UserNotFoundException(Number id) {
        super(String.format(MSG_PART, id.toString()));
    }
}
