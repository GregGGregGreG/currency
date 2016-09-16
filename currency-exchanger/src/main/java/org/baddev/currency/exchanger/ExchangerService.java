package org.baddev.currency.exchanger;

import org.baddev.currency.core.operation.DeleteOperation;
import org.baddev.currency.core.operation.UserRestrictedFindOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface ExchangerService extends UserRestrictedFindOperation<IExchangeOperation, Long>,
        DeleteOperation<Long>, ExchangeAction {
}
