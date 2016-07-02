package org.baddev.currency.core;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface MutableIdentity<T> extends Identity<T> {
    void setId(T id);
}
