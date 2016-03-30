package org.baddev.currency.fetcher;

import org.baddev.currency.fetcher.entity.ExchangeRate;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Currency;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface ExchangeRateFetcher<T extends ExchangeRate> {

    Collection<T> fetchCurrent();

    Collection<T> fetchByDate(LocalDate date);

    T fetchByCurrencyAndDate(Currency currency, LocalDate date);

}
