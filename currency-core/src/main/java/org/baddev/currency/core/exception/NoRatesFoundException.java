package org.baddev.currency.core.exception;

public final class NoRatesFoundException extends RuntimeException {

    public NoRatesFoundException(String message) {
        super(message);
    }

}