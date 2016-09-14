package org.baddev.currency.scheduler.task.exchange;

import org.baddev.currency.core.UserRestrictedLookup;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/12/2016.
 */
public interface ExchangeTaskService<T extends IExchangeTask> extends UserRestrictedLookup<T, Long> {
    void save(T exchangeTask);
    T saveReturning(T exchangeTask);
    Collection<T> findAll();
    void update(T exchangeTask);
    void deleteById(Long... ids);
}
