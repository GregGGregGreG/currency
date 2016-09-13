package org.baddev.currency.fetcher;

import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Currency;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface RateFetcherService<T extends IExchangeRate> {
    Collection<T> fetchCurrent();
    Collection<T> fetchByDate(LocalDate date);
    T fetchByCurrencyAndDate(Currency currency, LocalDate date);
    Collection<T> findAll();
    Collection<T> findLast();
}
