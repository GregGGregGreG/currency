package org.baddev.currency.core;

import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Currency;
import java.util.Optional;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface ExchangeRateFetcher {
    Collection<? extends IExchangeRate> fetchCurrent();
    Collection<? extends IExchangeRate> fetchByDate(LocalDate date);
    Optional<? extends IExchangeRate> fetchByCurrencyAndDate(Currency currency, LocalDate date);
}
