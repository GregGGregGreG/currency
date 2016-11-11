package org.baddev.currency.core.api;

import org.baddev.common.action.DeleteAction;
import org.baddev.currency.core.action.ExchangeAction;
import org.baddev.currency.core.action.UserRestrictedFindAction;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface ExchangerService extends UserRestrictedFindAction<IExchangeOperation, Long>,
        DeleteAction<Long>, ExchangeAction {
}
