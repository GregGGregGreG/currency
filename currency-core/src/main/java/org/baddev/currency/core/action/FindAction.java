package org.baddev.currency.core.action;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface FindAction<T, ID> {
    Collection<? extends T> findById(ID... ids);
    Optional<? extends T> findOneById(ID id);
    Collection<? extends T> findAll();
}
