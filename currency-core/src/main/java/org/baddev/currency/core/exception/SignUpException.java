package org.baddev.currency.core.exception;

/**
 * Created by IPotapchuk on 7/4/2016.
 */
public class SignUpException extends ServiceException {

    public SignUpException(String message) {
        super(message);
    }

    public SignUpException(String message, Throwable cause) {
        super(message, cause);
    }

}
