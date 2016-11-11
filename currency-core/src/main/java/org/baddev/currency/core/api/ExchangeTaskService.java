package org.baddev.currency.core.api;

import org.baddev.common.action.CreateAction;
import org.baddev.common.action.DeleteAction;
import org.baddev.common.action.UpdateAction;
import org.baddev.common.schedulling.ScheduledTaskManager;
import org.baddev.currency.core.action.UserRestrictedFindAction;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface ExchangeTaskService extends CreateAction<IExchangeTask>, UserRestrictedFindAction<IExchangeTask, Long>,
        UpdateAction<IExchangeTask>, DeleteAction<Long>, ScheduledTaskManager {
    IExchangeTask createReturning(IExchangeTask entity);
}
