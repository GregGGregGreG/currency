package org.baddev.currency.scheduler;

import org.baddev.currency.scheduler.entity.CronExchangeTaskData;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
public interface ScheduledExchangeManager extends CcyTaskScheduler<CronExchangeTaskData> {
    Map<CronExchangeTaskData, ScheduledFuture> getScheduledTasks();
    void reschedule(CronExchangeTaskData reschedulingData);
}
