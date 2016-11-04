package org.baddev.currency.core.api;

import org.baddev.currency.core.action.DeleteAction;
import org.baddev.currency.core.action.FindAction;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface ExchangeRateService extends FindAction<IExchangeRate, Long>, DeleteAction<Long>,
        ExchangeRateFetcher {
    Collection<? extends IExchangeRate> findLast();
}
