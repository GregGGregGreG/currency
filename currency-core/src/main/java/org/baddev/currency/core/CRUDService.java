package org.baddev.currency.core;

import org.baddev.currency.core.operation.DeleteOperation;
import org.baddev.currency.core.operation.SaveOperation;
import org.baddev.currency.core.operation.UpdateOperation;
import org.baddev.currency.core.operation.UserRestrictedFindOperation;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface CRUDService<T, ID> extends SaveOperation<T>, UserRestrictedFindOperation<T, ID>, UpdateOperation<T>, DeleteOperation<ID> {
    T saveReturning(T entity);
}
