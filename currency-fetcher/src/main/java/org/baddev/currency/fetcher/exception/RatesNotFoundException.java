package org.baddev.currency.fetcher.exception;

import org.baddev.currency.core.exception.ServiceException;

public final class RatesNotFoundException extends ServiceException {

    public RatesNotFoundException(String message) {
        super(message);
    }

    public RatesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}