package org.baddev.currency.fetcher.service;

import org.baddev.currency.core.operation.DeleteOperation;
import org.baddev.currency.core.operation.UserRestrictedFindOperation;
import org.baddev.currency.fetcher.ExchangeRateFetcher;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface ExchangeRateService extends UserRestrictedFindOperation<IExchangeRate, Long>, DeleteOperation<Long>,
        ExchangeRateFetcher {
    Collection<? extends IExchangeRate> findLast();
}
