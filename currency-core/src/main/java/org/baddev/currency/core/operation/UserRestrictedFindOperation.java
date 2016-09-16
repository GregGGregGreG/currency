package org.baddev.currency.core.operation;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/13/2016.
 */
public interface UserRestrictedFindOperation<T, E> extends FindOperation<T, E> {
    Collection<? extends T> findForUser(E key);
}
