package org.baddev.currency.scheduler.exchange.impl;

import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.scheduler.exchange.ExchangeTaskScheduler;
import org.baddev.currency.scheduler.exchange.service.ExchangeTaskService;
import org.baddev.currency.scheduler.exchange.task.NotifiableExchangeTask;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import static org.baddev.currency.jooq.schema.Tables.EXCHANGE_TASK;

/**
 * Created by IPOTAPCHUK on 6/8/2016.
 */
@Component
public class ExchangeTaskSchedulerImpl implements ExchangeTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExchangeTaskSchedulerImpl.class);

    @Autowired
    private DSLContext dsl;
    @Autowired
    private ThreadPoolTaskScheduler pool;
    @Autowired
    private ExchangeTaskService taskService;

    private Set<IExchangeTask> exchangeTasks = new HashSet<>();
    private Map<Long, ScheduledFuture> exchangeTasksJobsMap = new HashMap<>();

    @Override
    public Long schedule(NotifiableExchangeTask taskData) {
        IExchangeTask prepared = prepareSchedule(taskData.getTaskData());
        taskData.setTaskData(prepared);
        scheduleTask(taskData);
        return prepared.getId();
    }

    private IExchangeTask prepareSchedule(IExchangeTask taskData){
        IExchangeTask task = taskData.into(new ExchangeTask());
        task.setActive(true);
        if (exchangeTasks.contains(taskData)) {
            ScheduledFuture aged = exchangeTasksJobsMap.get(taskData.getId());
            if (aged != null) {
                if (!aged.isCancelled()) {
                    throw new IllegalArgumentException("Task " + taskData.getId() + " already exists");
                } else {
                    exchangeTasksJobsMap.remove(taskData.getId());
                }
            }
            taskService.update(task);
        } else {
            task = taskService.saveReturning(task);
        }
        return task;
    }

    private ScheduledFuture scheduleTask(NotifiableExchangeTask task) {
        ScheduledFuture scheduled = pool.schedule(task, new CronTrigger(task.getTaskData().getCron()));
        exchangeTasks.add(task.getTaskData());
        exchangeTasksJobsMap.put(task.getTaskData().getId(), scheduled);
        return scheduled;
    }

    @Override
    public void execute(NotifiableExchangeTask taskData) {
        pool.execute(taskData);
    }

    @Override
    public void cancel(Long id, boolean remove) {
        IExchangeTask taskData = exchangeTasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .get();
        if (taskData == null) return;
        taskData.setActive(false);
        ScheduledFuture task = exchangeTasksJobsMap.remove(id);
        if (task != null) task.cancel(false);
        if (remove) {
            exchangeTasks.remove(taskData);
            taskService.delete(taskData.getId());
        } else taskService.update(taskData.into(new ExchangeTask()));
    }

    @Override
    public void cancelAll(boolean remove) {
        exchangeTasksJobsMap.values().forEach(t -> t.cancel(false));
        exchangeTasksJobsMap.clear();
        if (remove) {
            exchangeTasks.clear();
            dsl.deleteFrom(EXCHANGE_TASK).execute();
        } else dsl.update(EXCHANGE_TASK).set(EXCHANGE_TASK.ACTIVE, false).execute();
    }

    @Override
    public int getActiveCount() {
        return dsl.fetchCount(dsl.selectFrom(EXCHANGE_TASK).where(EXCHANGE_TASK.ACTIVE.eq(true)));
    }

}
