package org.baddev.currency.exchange.dao;


import org.baddev.currency.exchange.entity.ExchangeOperation;

import java.util.Collection;

/**
 * Created by IPotapchuk on 3/15/2016.
 */
public interface ExchangeOperationDao {

    void save(ExchangeOperation record);

    void remove(Long id);

    ExchangeOperation load(Long id);

    Collection<ExchangeOperation> loadAll();

}
