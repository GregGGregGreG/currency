package org.baddev.currency;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface Identity<T> {

    T getId();

    void setId(T id);

}
