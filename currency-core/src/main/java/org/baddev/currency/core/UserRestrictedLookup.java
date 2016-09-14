package org.baddev.currency.core;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/13/2016.
 */
public interface UserRestrictedLookup<T, E> {
    Collection<T> findForUser(E key);
}
