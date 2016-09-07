package org.baddev.currency.scheduler;

import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
public interface ScheduledExchangeManager extends CcyTaskScheduler<ExchangeTask> {
    Collection<ExchangeTask> getExchangeTasks();
    Map<Long, ScheduledFuture> getJobsMap();
    void reschedule(ExchangeTask reschedulingData);
}
