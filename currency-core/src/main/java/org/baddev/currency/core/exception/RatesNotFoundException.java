package org.baddev.currency.core.exception;

public final class RatesNotFoundException extends ServiceException {

    public RatesNotFoundException(String message) {
        super(message);
    }

    public RatesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}