package org.baddev.currency.dao;

import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Created by IPotapchuk on 4/4/2016.
 */
public interface GenericDao<T> {

    void save(T record);
    void remove(Long id);
    T load(Long id);
    Collection<T> loadAll();
    Collection<T> loadByDate(LocalDate date);

}
