package org.baddev.currency.core.fetcher;

import org.baddev.currency.core.fetcher.entity.ExchangeRate;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Currency;

/**
 * Created by IPotapchuk on 3/14/2016.
 */

public interface ExchangeRateFetcher<T extends ExchangeRate> {

    Collection<T> fetchCurrent() throws NoRatesFoundException;

    Collection<T> fetchByDate(LocalDate date) throws NoRatesFoundException;

    T fetchByCurrencyAndDate(Currency currency, LocalDate date) throws NoRatesFoundException;

}
