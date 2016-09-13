package org.baddev.currency.security.user.impl;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/13/2016.
 */
public interface UserRestrictedLookup<T, E> {
    Collection<T> findForUser(E key);
}
