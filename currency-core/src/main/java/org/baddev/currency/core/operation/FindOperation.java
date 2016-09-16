package org.baddev.currency.core.operation;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface FindOperation<T, ID> {
    Collection<? extends T> find(ID... ids);
    T findOne(ID id);
    Collection<? extends T> findAll();
}
