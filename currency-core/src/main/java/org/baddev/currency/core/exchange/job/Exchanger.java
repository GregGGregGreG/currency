package org.baddev.currency.core.exchange.job;

import org.baddev.currency.core.exchange.entity.ExchangeOperation;
import org.baddev.currency.core.fetcher.entity.ExchangeRate;

import java.util.Collection;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface Exchanger {
    ExchangeOperation exchange(ExchangeOperation operation, Collection<ExchangeRate> rates);
}
