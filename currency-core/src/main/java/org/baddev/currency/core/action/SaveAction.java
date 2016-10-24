package org.baddev.currency.core.action;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface SaveAction<T> {
    void save(T entity);
}
