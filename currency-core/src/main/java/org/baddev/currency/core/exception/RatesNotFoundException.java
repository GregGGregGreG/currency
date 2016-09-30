package org.baddev.currency.core.exception;

public final class RatesNotFoundException extends RuntimeException {

    public RatesNotFoundException(String message) {
        super(message);
    }

}