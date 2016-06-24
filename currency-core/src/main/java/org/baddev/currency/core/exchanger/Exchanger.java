package org.baddev.currency.core.exchanger;

import org.baddev.currency.core.exchanger.entity.ExchangeOperation;
import org.baddev.currency.core.fetcher.entity.ExchangeRate;

import java.util.Collection;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface Exchanger {
    ExchangeOperation exchange(ExchangeOperation operation, Collection<ExchangeRate> rates);
}
