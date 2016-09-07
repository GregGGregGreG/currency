package org.baddev.currency.core.exception;

/**
 * Created by IPotapchuk on 3/15/2016.
 */
public class CurrencyNotFoundException extends Exception {

    public CurrencyNotFoundException() {
        super();
    }

    public CurrencyNotFoundException(String message) {
        super(message);
    }

    public CurrencyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
