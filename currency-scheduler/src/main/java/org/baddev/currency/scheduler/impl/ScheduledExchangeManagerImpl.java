package org.baddev.currency.scheduler.impl;

import org.baddev.currency.core.exchanger.Exchanger;
import org.baddev.currency.core.exchanger.entity.ExchangeOperation;
import org.baddev.currency.core.fetcher.ExchangeRateFetcher;
import org.baddev.currency.core.fetcher.NoRatesFoundException;
import org.baddev.currency.fetcher.impl.nbu.NBU;
import org.baddev.currency.notifier.Notifier;
import org.baddev.currency.notifier.event.ExchangeCompletionEvent;
import org.baddev.currency.scheduler.ScheduledExchangeManager;
import org.baddev.currency.scheduler.entity.CronExchangeTaskData;
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
    private Exchanger exchanger;
    @NBU
    private ExchangeRateFetcher fetcher;
    @Autowired
    private DSLContext dsl;
    @Autowired
    private ThreadPoolTaskScheduler scheduler;
    @Autowired
    private Notifier notifier;

    private Map<CronExchangeTaskData, ScheduledFuture> activeCronTasks = new HashMap<>();

    private class ExchangeTask implements Runnable {

        private ExchangeOperation operation;

        ExchangeTask(final CronExchangeTaskData taskData) {
            operation = ExchangeOperation.newBuilder()
                    .id(taskData.getId())
                    .from(taskData.getFromCcy())
                    .to(taskData.getToCcy())
                    .amount(taskData.getAmount())
                    .ratesDate(LocalDate.now())
                    .build();
        }

        @Override
        public void run() {
            boolean success = true;
            try {
                exchanger.exchange(operation, fetcher.fetchCurrent());
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
        List<Long> canceled = new ArrayList<>();
        List<CronExchangeTaskData> ops = dsl.selectFrom(EXCHANGE_TASK)
                .fetch(record -> {
                    CronExchangeTaskData taskData = CronExchangeTaskData.newBuilder()
                            .id(record.getId())
                            .fromCcy(record.getFromCcy())
                            .toCcy(record.getToCcy())
                            .amount(record.getAmount())
                            .cron(record.getCron())
                            .addedDate(record.getAddedDatetime())
                            .build();
                    if (record.getActive().intValue() == 0)
                        canceled.add(taskData.getId());
                    return taskData;
                });
        if (!ops.isEmpty()) {
            ops.forEach(op -> {
                ScheduledFuture task = scheduleTask(op);
                if (canceled.contains(op.getId()))
                    task.cancel(false);
            });
            log.info("{} task(s) loaded and scheduled", ops.size());
        }
    }

    @Override
    @Transactional
    public Long schedule(final CronExchangeTaskData taskData) {
        if (taskData.getId() == null)
            throw new IllegalArgumentException("Id must be a non-null value");
        persist(taskData);
        scheduleTask(taskData);
        return taskData.getId();
    }

    private void persist(CronExchangeTaskData taskData) {
        dsl.insertInto(EXCHANGE_TASK)
                .set(EXCHANGE_TASK.ID, taskData.getId())
                .set(EXCHANGE_TASK.AMOUNT, taskData.getAmount())
                .set(EXCHANGE_TASK.ADDED_DATETIME, taskData.getAddedDate())
                .set(EXCHANGE_TASK.TO_CCY, taskData.getToCcy())
                .set(EXCHANGE_TASK.FROM_CCY, taskData.getFromCcy())
                .set(EXCHANGE_TASK.CRON, taskData.getCron())
                .execute();
    }

    private ScheduledFuture scheduleTask(final CronExchangeTaskData op) {
        ScheduledFuture scheduled = scheduler.schedule(new ExchangeTask(op), new CronTrigger(op.getCron()));
        activeCronTasks.put(op, scheduled);
        return scheduled;
    }

    @Override
    @Transactional
    public void reschedule(CronExchangeTaskData reschedulingData) {
        if (!activeCronTasks.containsKey(reschedulingData))
            throw new IllegalArgumentException("Can't reschedule unknown task");
        if (!activeCronTasks.get(reschedulingData).isCancelled())
            throw new IllegalArgumentException("Can't reschedule. Given task is already running");
        scheduleTask(reschedulingData);
        dsl.update(EXCHANGE_TASK)
                .set(EXCHANGE_TASK.ACTIVE, Byte.parseByte("1"))
                .where(EXCHANGE_TASK.ID.eq(reschedulingData.getId()))
                .execute();
    }

    @Override
    public void execute(CronExchangeTaskData taskData) {
        scheduler.execute(new ExchangeTask(taskData));
    }

    @Override
    @Transactional
    public boolean cancel(Long id, boolean remove) {
        Optional<CronExchangeTaskData> found = activeCronTasks.keySet().stream()
                .filter(op -> Objects.equals(op.getId(), id))
                .findFirst();
        if (!found.isPresent())
            return false;
        CronExchangeTaskData taskData = found.get();
        ScheduledFuture task = activeCronTasks.get(taskData);
        boolean result = task.cancel(false);
        if (remove) {
            dsl.deleteFrom(EXCHANGE_TASK).where(EXCHANGE_TASK.ID.eq(id)).execute();
            activeCronTasks.remove(taskData);
        }
        dsl.update(EXCHANGE_TASK)
                .set(EXCHANGE_TASK.ACTIVE, Byte.parseByte("0"))
                .where(EXCHANGE_TASK.ID.eq(id))
                .execute();
        return result;
    }

    @Override
    @Transactional
    public void cancelAll(boolean remove) {
        activeCronTasks.values().forEach(t -> t.cancel(false));
        activeCronTasks.clear();
        if (remove)
            dsl.deleteFrom(EXCHANGE_TASK).execute();
        dsl.update(EXCHANGE_TASK).set(EXCHANGE_TASK.ACTIVE, Byte.parseByte("0")).execute();
    }

    @Override
    public Map<CronExchangeTaskData, ScheduledFuture> getScheduledTasks() {
        return activeCronTasks;
    }

    @Override
    public int getActiveCount() {
        return activeCronTasks.size() - (int) activeCronTasks.values().stream().filter(Future::isCancelled).count();
    }

}
