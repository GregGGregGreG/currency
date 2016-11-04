package org.baddev.currency.fetcher;

import org.baddev.currency.core.api.ExchangeRateFetcher;
import org.baddev.currency.core.meta.Dev;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by IPotapchuk on 10/31/2016.
 */
@Dev @Component
public class FakeFetcher implements ExchangeRateFetcher {

    @Override
    public Collection<? extends IExchangeRate> fetchCurrent() {
        return Arrays.asList(
                new ExchangeRate(0L, "UAH", "USD", LocalDate.now(), 26d),
                new ExchangeRate(1L, "UAH", "RUR", LocalDate.now(), 0.38d),
                new ExchangeRate(2L, "UAH", "EUR", LocalDate.now(), 28d));
    }

    @Override
    public Collection<? extends IExchangeRate> fetchByDate(LocalDate date) {
        return Arrays.asList(
                new ExchangeRate(0L, "UAH", "USD", date, 26d),
                new ExchangeRate(1L, "UAH", "RUR", date, 0.38d),
                new ExchangeRate(2L, "UAH", "EUR", date, 28d));
    }

    @Override
    public Optional<? extends IExchangeRate> fetchByCurrencyAndDate(Currency currency, LocalDate date) {
        return Optional.of(new ExchangeRate(1L, "UAH", currency.getCurrencyCode(), date, ThreadLocalRandom.current().nextDouble()));
    }
}
