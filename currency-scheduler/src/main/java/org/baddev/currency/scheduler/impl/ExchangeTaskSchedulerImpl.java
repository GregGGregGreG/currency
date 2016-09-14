package org.baddev.currency.scheduler.impl;

import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.core.notifier.Notifier;
import org.baddev.currency.exchanger.ExchangerService;
import org.baddev.currency.fetcher.RateFetcherService;
import org.baddev.currency.fetcher.impl.nbu.NBU;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.scheduler.ExchangeTaskScheduler;
import org.baddev.currency.scheduler.task.exchange.ExchangeTaskService;
import org.joda.time.LocalDate;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

import static org.baddev.currency.jooq.schema.Tables.EXCHANGE_TASK;

/**
 * Created by IPOTAPCHUK on 6/8/2016.
 */
@Service
public class ExchangeTaskSchedulerImpl implements ExchangeTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExchangeTaskSchedulerImpl.class);

    @Autowired
    private ExchangerService<ExchangeOperation, ExchangeRate> exchanger;
    @NBU
    private RateFetcherService<ExchangeRate> fetcher;
    @Autowired
    private DSLContext dsl;
    @Autowired
    private ThreadPoolTaskScheduler pool;
    @Autowired
    private Notifier notifier;
    @Autowired
    private ExchangeTaskService<ExchangeTask> exchangeTaskService;

    private Set<ExchangeTask> exchangeTasks = new HashSet<>();
    private Map<Long, ScheduledFuture> exchangeTasksJobsMap = new HashMap<>();

    private class ExchangeJob implements Runnable {

        private ExchangeTask taskData;
        private boolean success = true;

        ExchangeJob(final ExchangeTask taskData) {
            this.taskData = taskData;
        }

        @Override
        public void run() {
            ExchangeOperation exchOp = new ExchangeOperation()
                    .setUserId(taskData.getUserId())
                    .setFromCcy(taskData.getFromCcy())
                    .setToCcy(taskData.getToCcy())
                    .setFromAmount(taskData.getAmount())
                    .setRatesDate(LocalDate.now());
            try {
                exchOp = exchanger.exchange(exchOp, fetcher.fetchCurrent());
            } catch (Exception e) {
                success = false;
                log.error("Error while performing exchange", e);
            } finally {
                if (!notifier.getSubscribers().isEmpty())
                    notifier.doNotify(new ExchangeCompletionEvent(this, exchOp, success));
            }
        }

    }

    @PostConstruct
    @Transactional(readOnly = true)
    public void init() {
        Collection<ExchangeTask> tasks = exchangeTaskService.findAll();
        tasks.forEach(t -> {
            ScheduledFuture task = scheduleTask(t);
            if (!t.getActive())
                task.cancel(false);
        });
        log.info("{} task(s) loaded and scheduled", tasks.size());
    }

    @Override
    @Transactional
    public Long schedule(final ExchangeTask taskData) {
        ExchangeTask task = new ExchangeTask(taskData).setActive(true);
        if (exchangeTasks.contains(taskData)) {
            ScheduledFuture aged = exchangeTasksJobsMap.get(taskData.getId());
            if (aged != null) {
                if (!aged.isCancelled()) {
                    throw new IllegalArgumentException("Task " + taskData.getId() + " already exists");
                } else {
                    exchangeTasksJobsMap.remove(taskData.getId());
                }
            }
            exchangeTaskService.update(task);
        } else {
            task = exchangeTaskService.saveReturning(task);
        }
        scheduleTask(task);
        return task.getId();
    }

    private ScheduledFuture scheduleTask(final ExchangeTask op) {
        ScheduledFuture scheduled = pool.schedule(new ExchangeJob(op), new CronTrigger(op.getCron()));
        exchangeTasks.add(op);
        exchangeTasksJobsMap.put(op.getId(), scheduled);
        return scheduled;
    }

    @Override
    public void execute(ExchangeTask taskData) {
        pool.execute(new ExchangeJob(taskData));
    }

    @Override
    @Transactional
    public boolean cancel(Long id, boolean remove) {
        ExchangeTask taskData = exchangeTasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task with id " + id + " not found"));
        taskData.setActive(false);
        ScheduledFuture task = exchangeTasksJobsMap.get(id);
        boolean result = task.cancel(false);
        if (remove) {
            result &= exchangeTasks.remove(taskData);
            exchangeTaskService.deleteById(id);
        } else {
            exchangeTaskService.update(taskData);
        }
        exchangeTasksJobsMap.remove(id);
        return result;
    }

    @Override
    @Transactional
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
        return exchangeTasksJobsMap.size();
    }

}
