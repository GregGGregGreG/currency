package org.baddev.currency.scheduler;

import org.baddev.currency.core.TaskManager;
import org.baddev.currency.core.task.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by IPotapchuk on 10/20/2016.
 */
public class TaskManagerImpl implements TaskManager {

    private static final Logger log = LoggerFactory.getLogger(TaskManagerImpl.class);

    private ThreadPoolTaskScheduler scheduler;
    private Map<Long, ScheduledFuture> taskIdToFutureMap = new HashMap<>();

    public TaskManagerImpl(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void schedule(AbstractTask task, CronTrigger trigger) {
        if (taskIdToFutureMap.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task with given ID already scheduled: " + task.getId());
        }
        ScheduledFuture future = scheduler.schedule(task, trigger);
        taskIdToFutureMap.put(task.getId(), future);
        log.debug("Task {} scheduled", task.getId());
    }

    @Override
    public void execute(AbstractTask task) {
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

}
