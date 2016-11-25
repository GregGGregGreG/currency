package org.baddev.currency.scheduler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.baddev.common.schedulling.ScheduledTaskManager;
import org.baddev.common.schedulling.task.AbstractTask;
import org.slf4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by IPotapchuk on 10/20/2016.
 */
@RequiredArgsConstructor
public class ScheduledTaskManagerImpl implements ScheduledTaskManager {

    private static final long serialVersionUID = -1532514615699072681L;

    private final Logger log;
    private final ThreadPoolTaskScheduler scheduler;

    private Map<Long, ScheduledFuture> taskIdToFutureMap = Collections.synchronizedMap(new HashMap<>());
    private Set<AbstractTask> tasks = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void schedule(@NonNull AbstractTask task, @NonNull String cron) {
        if (taskIdToFutureMap.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task with given ID already scheduled: " + task.getId());
        }
        ScheduledFuture future = scheduler.schedule(task, new CronTrigger(cron));
        taskIdToFutureMap.put(task.getId(), future);
        log.debug("Task {} scheduled", task.getId());
    }

    @Override
    public void execute(@NonNull AbstractTask task) {
        scheduler.execute(task);
        log.debug("Task {} executed", task.getId());
    }

    @Override
    public void terminate(@NonNull Long taskId) {
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
    public boolean isScheduled(@NonNull Long id) {
        return taskIdToFutureMap.containsKey(id);
    }

    @Override
    public Collection<AbstractTask> getScheduledTasks() {
        return tasks;
    }

    @Override
    public Optional<ScheduledFuture> getFuture(@NonNull Long taskId) {
        return Optional.ofNullable(taskIdToFutureMap.get(taskId));
    }

}
