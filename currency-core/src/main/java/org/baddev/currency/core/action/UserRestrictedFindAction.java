package org.baddev.currency.core.action;

import org.baddev.common.action.FindAction;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/13/2016.
 */
public interface UserRestrictedFindAction<T, ID> extends FindAction<T, ID> {
    Collection<? extends T> findForUser(ID key);
}
