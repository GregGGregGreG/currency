package org.baddev.currency.scheduler.impl;

import org.baddev.currency.core.exchange.entity.ExchangeOperation;
import org.baddev.currency.core.exchange.job.Exchanger;
import org.baddev.currency.core.fetcher.ExchangeRateFetcher;
import org.baddev.currency.core.fetcher.NoRatesFoundException;
import org.baddev.currency.dao.utils.ConverterUtils;
import org.baddev.currency.fetcher.impl.nbu.NBU;
import org.baddev.currency.scheduler.CronExchangeOperation;
import org.baddev.currency.scheduler.ScheduledExchangeManager;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
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

    @Resource(name = "taskScheduler")
    private ThreadPoolTaskScheduler scheduler;

    private Map<CronExchangeOperation, ScheduledFuture> activeCronTasks = new HashMap<>();

    private class ExchangeTask implements Runnable {

        private ExchangeOperation operation;

        public ExchangeTask(ExchangeOperation operation) {
            this.operation = operation;
        }

        @Override
        public void run() {
            try {
                exchanger.exchange(operation, fetcher.fetchCurrent());
            } catch (NoRatesFoundException e) {
                log.error("Rates are not available", e);
            }
        }
    }

    @PostConstruct
    @Transactional(readOnly = true)
    public void init() {
        List<CronExchangeOperation> ops = dsl.selectFrom(EXCHANGE_TASK)
                .fetch(record -> {
                    ExchangeOperation op = ExchangeOperation.newBuilder()
                            .id(record.getId())
                            .amount(record.getAmount())
                            .date(ConverterUtils.fromSqlDate(record.getDateAdded()))
                            .from(record.getFromCcy())
                            .to(record.getToCcy())
                            .build();
                    return new CronExchangeOperation(record.getCron(), op);
                });
        if (ops.size() > 0) {
            ops.forEach(this::schedule);
            log.info("{} tasks loaded and scheduled", ops.size());
        }
    }

    private void schedule(CronExchangeOperation op) {
        ScheduledFuture task = scheduler.schedule(new ExchangeTask(op), new CronTrigger(op.getCron()));
        activeCronTasks.put(op, task);
    }

    @Override
    @Transactional
    public Long schedule(final ExchangeOperation taskData, String cron) {
        if (taskData.getId() == null)
            throw new IllegalArgumentException("Id must be a non-null value");
        dsl.insertInto(EXCHANGE_TASK)
                .set(EXCHANGE_TASK.ID, taskData.getId())
                .set(EXCHANGE_TASK.AMOUNT, taskData.getAmount())
                .set(EXCHANGE_TASK.DATE_ADDED, ConverterUtils.toSqlDate(taskData.getDate()))
                .set(EXCHANGE_TASK.TO_CCY, taskData.getExchangedAmountCurrencyCode())
                .set(EXCHANGE_TASK.FROM_CCY, taskData.getAmountCurrencyCode())
                .set(EXCHANGE_TASK.CRON, cron)
                .execute();
        schedule(new CronExchangeOperation(cron, taskData));
        return taskData.getId();
    }

    @Override
    public void execute(ExchangeOperation taskData) {
        scheduler.execute(new ExchangeTask(taskData));
    }

    @Override
    @Transactional
    public boolean cancel(Long id) {
        Optional<CronExchangeOperation> found = activeCronTasks.keySet().stream()
                .filter(op -> Objects.equals(op.getId(), id))
                .findFirst();
        if (!found.isPresent())
            return false;
        ScheduledFuture task = activeCronTasks.remove(found.get());
        boolean result = task.cancel(false);
        dsl.deleteFrom(EXCHANGE_TASK).where(EXCHANGE_TASK.ID.eq(id)).execute();
        return result;
    }

    @Override
    @Transactional
    public void cancelAll() {
        activeCronTasks.values().forEach(t -> t.cancel(false));
        activeCronTasks.clear();
        dsl.deleteFrom(EXCHANGE_TASK).execute();
    }

    @Override
    public Map<CronExchangeOperation, ScheduledFuture> getScheduledTasks() {
        return activeCronTasks;
    }

    @Override
    public int getActiveCount() {
        return activeCronTasks.size();
    }

}
