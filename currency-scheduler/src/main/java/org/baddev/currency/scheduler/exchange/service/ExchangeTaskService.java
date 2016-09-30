package org.baddev.currency.scheduler.exchange.service;

import org.baddev.currency.core.CRUDService;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.scheduler.exchange.ExchangeTaskScheduler;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface ExchangeTaskService extends CRUDService<IExchangeTask, Long>, ExchangeTaskScheduler {
    int getActiveCountByUser(Long key);
}
