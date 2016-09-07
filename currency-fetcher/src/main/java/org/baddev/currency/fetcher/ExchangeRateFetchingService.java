package org.baddev.currency.fetcher;

import org.baddev.currency.core.exception.NoRatesFoundException;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Currency;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface ExchangeRateFetchingService<T extends IExchangeRate> {

    Collection<T> fetchCurrent() throws NoRatesFoundException;

    Collection<T> fetchByDate(LocalDate date) throws NoRatesFoundException;

    T fetchByCurrencyAndDate(Currency currency, LocalDate date) throws NoRatesFoundException;

}
