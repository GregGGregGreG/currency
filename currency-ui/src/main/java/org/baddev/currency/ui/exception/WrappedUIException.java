package org.baddev.currency.ui.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Created by IPotapchuk on 10/6/2016.
 */
public class WrappedUIException extends NestedRuntimeException {

    public WrappedUIException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public WrappedUIException(Throwable cause){
        super(cause.getMessage(), cause);
    }

}
