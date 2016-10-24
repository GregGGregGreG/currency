package org.baddev.currency.core;

import org.baddev.currency.core.action.DeleteAction;
import org.baddev.currency.core.action.SaveAction;
import org.baddev.currency.core.action.UpdateAction;
import org.baddev.currency.core.action.UserRestrictedFindAction;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface CRUDService<T, ID> extends SaveAction<T>, UserRestrictedFindAction<T, ID>, UpdateAction<T>, DeleteAction<ID> {
    T saveReturning(T entity);
}
