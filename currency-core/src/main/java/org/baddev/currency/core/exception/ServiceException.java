package org.baddev.currency.core.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Created by Ilya on 23.04.2016.
 */
public class ServiceException extends NestedRuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
