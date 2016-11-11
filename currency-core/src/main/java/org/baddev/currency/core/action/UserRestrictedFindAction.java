package org.baddev.currency.core.action;

import org.baddev.common.action.FindAction;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/13/2016.
 */
public interface UserRestrictedFindAction<T, E> extends FindAction<T, E> {
    Collection<? extends T> findForUser(E key);
}
