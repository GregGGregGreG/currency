package org.baddev.currency.core.task;

import org.springframework.scheduling.support.CronTrigger;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by IPotapchuk on 10/20/2016.
 */
public interface TaskManager extends Serializable {
    void schedule(AbstractTask task, CronTrigger trigger);
    void execute(AbstractTask task);
    void terminate(Long taskId);
    void terminateAll();
    int getActiveCount();
    boolean isScheduled(Long id);
    Collection<AbstractTask> getScheduledTasks();
    Optional<ScheduledFuture> getFuture(Long taskId);
}
