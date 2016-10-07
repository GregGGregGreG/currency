package org.baddev.currency.fetcher.iso4217.exception;

import org.baddev.currency.core.exception.ServiceException;

/**
 * Created by IPotapchuk on 3/15/2016.
 */
public class CurrencyNotFoundException extends ServiceException {

    public CurrencyNotFoundException(String message) {
        super(message);
    }

    public CurrencyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
