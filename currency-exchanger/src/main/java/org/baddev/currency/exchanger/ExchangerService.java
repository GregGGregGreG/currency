package org.baddev.currency.exchanger;

import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;

import java.util.Collection;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface ExchangerService<T extends IExchangeOperation  ,E extends IExchangeRate> {
    T exchange(T operation, Collection<E> rates);
}
