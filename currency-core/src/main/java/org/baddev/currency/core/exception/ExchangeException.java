package org.baddev.currency.core.exception;

import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;

/**
 * Created by IPotapchuk on 10/7/2016.
 */
public class ExchangeException extends ServiceException {

    private IExchangeOperation details;

    public ExchangeException(String message, IExchangeOperation operation) {
        super(message);
        details = operation;
    }

    public ExchangeException(String message, Throwable cause, IExchangeOperation operation) {
        super(message, cause);
        details = operation;
    }

    public IExchangeOperation getDetails() {
        return details;
    }
}
