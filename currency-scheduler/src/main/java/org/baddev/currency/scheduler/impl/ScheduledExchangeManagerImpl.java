package org.baddev.currency.scheduler.impl;

import org.baddev.currency.core.exchange.entity.ExchangeOperation;
import org.baddev.currency.core.exchange.job.Exchanger;
import org.baddev.currency.core.fetcher.ExchangeRateFetcher;
import org.baddev.currency.core.fetcher.NoRatesFoundException;
import org.baddev.currency.fetcher.impl.nbu.NBU;
import org.baddev.currency.scheduler.CronExchangeOperation;
import org.baddev.currency.scheduler.ScheduledExchangeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

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

    @Override
    public Long schedule(final ExchangeOperation data, String cron) {
        if (data.getId() == null)
            throw new IllegalArgumentException("Id must be a non-null value");
        ScheduledFuture task = scheduler.schedule(new ExchangeTask(data), new CronTrigger(cron));
        activeCronTasks.put(new CronExchangeOperation(cron, data), task);
        return data.getId();
    }

    @Override
    public void execute(ExchangeOperation operation) {
        scheduler.execute(new ExchangeTask(operation));
    }

    @Override
    public boolean cancel(Long id) {
        Optional<CronExchangeOperation> found = activeCronTasks.keySet().stream()
                .filter(op -> Objects.equals(op.getId(), id))
                .findFirst();
        if (!found.isPresent())
            return false;
        ScheduledFuture task = activeCronTasks.remove(found.get());
        return task.cancel(false);
    }

    @Override
    public void cancelAll() {
        activeCronTasks.values().forEach(t -> t.cancel(false));
        activeCronTasks.clear();
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
