package org.baddev.currency.scheduler;

import org.baddev.currency.core.exchange.entity.ExchangeOperation;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
public interface ScheduledExchangeManager extends CcyTaskScheduler<ExchangeOperation> {
    Map<CronExchangeOperation, ScheduledFuture> getScheduledTasks();
}
