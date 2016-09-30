package org.baddev.currency.core.operation;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface FindOperation<T, ID> {
    Collection<? extends T> findById(ID... ids);
    Optional<T> findOneById(ID id);
    Collection<? extends T> findAll();
}
