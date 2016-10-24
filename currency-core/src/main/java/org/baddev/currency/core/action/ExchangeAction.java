package org.baddev.currency.core.action;

import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface ExchangeAction {
    Optional<IExchangeOperation> exchange(IExchangeOperation operation, Collection<? extends IExchangeRate> rates);
}
