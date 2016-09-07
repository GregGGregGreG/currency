package org.baddev.currency.scheduler.impl;

import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.core.exception.NoRatesFoundException;
import org.baddev.currency.core.notifier.Notifier;
import org.baddev.currency.exchanger.ExchangerService;
import org.baddev.currency.fetcher.ExchangeRateFetchingService;
import org.baddev.currency.fetcher.impl.nbu.NBU;
import org.baddev.currency.jooq.schema.tables.daos.ExchangeTaskDao;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.scheduler.ScheduledExchangeManager;
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
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import static org.baddev.currency.jooq.schema.Tables.EXCHANGE_TASK;

/**
 * Created by IPOTAPCHUK on 6/8/2016.
 */
@Service
public class ScheduledExchangeManagerImpl implements ScheduledExchangeManager {

    private static final Logger log = LoggerFactory.getLogger(ScheduledExchangeManagerImpl.class);

    @Autowired
    private ExchangerService<ExchangeOperation, ExchangeRate> exchanger;
    @NBU
    private ExchangeRateFetchingService<ExchangeRate> fetcher;
    @Autowired
    private DSLContext dsl;
    @Autowired
    private ThreadPoolTaskScheduler pool;
    @Autowired
    private Notifier notifier;
    @Autowired
    private ExchangeTaskDao exchangeTaskDao;

    private Set<ExchangeTask> exchangeTasks = new HashSet<>();
    private Map<Long, ScheduledFuture> exchangeTasksJobsMap = new HashMap<>();

    private class ExchangeJob implements Runnable {

        private ExchangeOperation operation;

        ExchangeJob(final ExchangeTask taskData) {
            operation = new ExchangeOperation()
                    .setFromCcy(taskData.getFromCcy())
                    .setToCcy(taskData.getToCcy())
                    .setFromAmount(taskData.getAmount())
                    .setRatesDate(LocalDate.now());
        }

        @Override
        public void run() {
            boolean success = true;
            try {
                operation = exchanger.exchange(operation, fetcher.fetchCurrent());
            } catch (NoRatesFoundException e) {
                success = false;
                log.error("Rates are not available", e);
            } finally {
                if (!notifier.getSubscribers().isEmpty())
                    notifier.doNotify(new ExchangeCompletionEvent(this, operation, success));
            }
        }

    }

    @PostConstruct
    @Transactional(readOnly = true)
    public void init() {
        List<ExchangeTask> tasks = exchangeTaskDao.findAll();
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
        if (taskData.getId() == null)
            throw new IllegalArgumentException("Id must be a non-null value");
        if (taskData.getActive() == null || !taskData.getActive())
            taskData.setActive(true);
        exchangeTaskDao.insert(taskData);
        scheduleTask(taskData);
        return taskData.getId();
    }

    private ScheduledFuture scheduleTask(final ExchangeTask op) {
        ScheduledFuture scheduled = pool.schedule(new ExchangeJob(op), new CronTrigger(op.getCron()));
        exchangeTasks.add(op);
        exchangeTasksJobsMap.put(op.getId(), scheduled);
        return scheduled;
    }

    @Override
    @Transactional
    public void reschedule(ExchangeTask reschedulingData) {
        if (reschedulingData.getId() == null)
            throw new IllegalArgumentException("Id must be a non-null value");
        if (!exchangeTasks.contains(reschedulingData))
            throw new IllegalArgumentException("Can't reschedule unknown task");
        if (!exchangeTasksJobsMap.containsKey(reschedulingData.getId()))
            throw new IllegalStateException("Task data exists but linked job was not found");
        ScheduledFuture task = exchangeTasksJobsMap.get(reschedulingData.getId());
        if (!task.isCancelled())
            throw new IllegalArgumentException("Can't reschedule. Given task is already scheduled");
        if (reschedulingData.getActive() && task.isCancelled())
            throw new IllegalStateException("Task's data state is not synchronized with pool task's state");
        scheduleTask(reschedulingData);
        exchangeTaskDao.update(reschedulingData.setActive(true));
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
            exchangeTaskDao.deleteById(id);
            result &= exchangeTasks.remove(taskData);
            result &= exchangeTasksJobsMap.remove(id) != null;
        } else exchangeTaskDao.update(taskData);
        return result;
    }

    @Override
    @Transactional
    public void cancelAll(boolean remove) {
        exchangeTasksJobsMap.values().forEach(t -> t.cancel(false));
        exchangeTasksJobsMap.clear();
        exchangeTasks.clear();
        if (remove)
            dsl.deleteFrom(EXCHANGE_TASK).execute();
        else dsl.update(EXCHANGE_TASK).set(EXCHANGE_TASK.ACTIVE, false).execute();
    }

    @Override
    public Map<Long, ScheduledFuture> getJobsMap() {
        return Collections.unmodifiableMap(exchangeTasksJobsMap);
    }

    public Collection<ExchangeTask> getExchangeTasks() {
        return Collections.unmodifiableSet(exchangeTasks);
    }

    @Override
    public int getActiveCount() {
        return exchangeTasksJobsMap.size() - (int) exchangeTasksJobsMap.values().stream().filter(Future::isCancelled).count();
    }

}
