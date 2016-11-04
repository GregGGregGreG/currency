package org.baddev.currency.core.api;

import org.baddev.currency.core.action.CreateAction;
import org.baddev.currency.core.action.DeleteAction;
import org.baddev.currency.core.action.UpdateAction;
import org.baddev.currency.core.action.UserRestrictedFindAction;
import org.baddev.currency.core.task.TaskManager;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface ExchangeTaskService extends CreateAction<IExchangeTask>, UserRestrictedFindAction<IExchangeTask, Long>,
        UpdateAction<IExchangeTask>, DeleteAction<Long>, TaskManager {
    IExchangeTask createReturning(IExchangeTask entity);
}
