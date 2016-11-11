package org.baddev.common.action;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface CreateAction<T> {
    void create(T entity);
}
