package org.baddev.currency.ui.currency.exchange.job;

import org.baddev.currency.ui.currency.exchange.entity.ExchangeOperation;
import org.baddev.currency.ui.currency.fetcher.entity.ExchangeRate;

import java.util.Collection;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface Exchanger {

    double exchange(ExchangeOperation operation, Collection<ExchangeRate> rates) throws Exception;

}
