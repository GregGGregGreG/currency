package org.baddev.currency.scheduler;

import org.baddev.common.schedulling.ScheduledTaskManager;
import org.baddev.common.schedulling.task.AbstractTask;
import org.slf4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by IPotapchuk on 10/20/2016.
 */
public class ScheduledTaskManagerImpl implements ScheduledTaskManager {

    private static final long serialVersionUID = -1532514615699072681L;

    private Logger log;

    private ThreadPoolTaskScheduler scheduler;

    private Map<Long, ScheduledFuture> taskIdToFutureMap = Collections.synchronizedMap(new HashMap<>());
    private Set<AbstractTask> tasks = Collections.synchronizedSet(new HashSet<>());

    public ScheduledTaskManagerImpl(ThreadPoolTaskScheduler scheduler, Logger log) {
        Assert.notNull(scheduler, "scheduler can't be null");
        Assert.notNull(log, "logger can't be null");
        this.scheduler = scheduler;
        this.log = log;
    }

    @Override
    public void schedule(AbstractTask task, String cron) {
        Assert.notNull(task, "task can't be null");
        Assert.notNull(cron, "cron can't be null");
        if (taskIdToFutureMap.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task with given ID already scheduled: " + task.getId());
        }
        ScheduledFuture future = scheduler.schedule(task, new CronTrigger(cron));
        taskIdToFutureMap.put(task.getId(), future);
        log.debug("Task {} scheduled", task.getId());
    }

    @Override
    public void execute(AbstractTask task) {
        Assert.notNull("task can't be null");
        scheduler.execute(task);
        log.debug("Task {} executed", task.getId());
    }

    @Override
    public void terminate(Long taskId) {
        if (taskIdToFutureMap.containsKey(taskId)) {
            ScheduledFuture future = taskIdToFutureMap.remove(taskId);
            future.cancel(false);
            log.debug("Task {} terminated", taskId);
        }
    }

    @Override
    public void terminateAll() {
        taskIdToFutureMap.values().forEach(future -> future.cancel(false));
        taskIdToFutureMap.clear();
        log.debug("All tasks terminated");
    }

    @Override
    public int getActiveCount() {
        return taskIdToFutureMap.size();
    }

    @Override
    public boolean isScheduled(Long id) {
        return taskIdToFutureMap.containsKey(id);
    }

    @Override
    public Collection<AbstractTask> getScheduledTasks() {
        return tasks;
    }

    @Override
    public Optional<ScheduledFuture> getFuture(Long taskId) {
        return Optional.ofNullable(taskIdToFutureMap.get(taskId));
    }

}
