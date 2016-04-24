package org.baddev.currency.core;

/**
 * Created by Ilya on 23.04.2016.
 */
public class ServiceException extends RuntimeException {

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }
}
