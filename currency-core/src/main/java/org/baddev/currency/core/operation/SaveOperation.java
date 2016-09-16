package org.baddev.currency.core.operation;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface SaveOperation<T> {
    void save(T entity);
}
