package org.baddev.currency.scheduler.exchange;

import org.baddev.currency.scheduler.CcyScheduler;
import org.baddev.currency.scheduler.exchange.task.NotifiableExchangeTask;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
public interface ExchangeTaskScheduler extends CcyScheduler<NotifiableExchangeTask, Long> {
}
