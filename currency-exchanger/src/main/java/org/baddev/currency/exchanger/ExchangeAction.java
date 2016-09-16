package org.baddev.currency.exchanger;

import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface ExchangeAction {
    IExchangeOperation exchange(IExchangeOperation operation, Collection<? extends IExchangeRate> rates);
}
