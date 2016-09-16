package org.baddev.currency.core.operation;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface DeleteOperation<ID> {
    void delete(ID... ids);
}
