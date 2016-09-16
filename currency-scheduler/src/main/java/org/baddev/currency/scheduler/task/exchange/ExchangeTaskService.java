package org.baddev.currency.scheduler.task.exchange;

import org.baddev.currency.core.CRUDService;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface ExchangeTaskService extends CRUDService<IExchangeTask, Long>, ExchangeTaskScheduler {
}
