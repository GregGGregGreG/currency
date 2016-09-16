package org.baddev.currency.scheduler.task.exchange;

import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.scheduler.CcyScheduler;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
public interface ExchangeTaskScheduler extends CcyScheduler<IExchangeTask, Long> {
}
