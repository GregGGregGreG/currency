package org.baddev.common.schedulling;

import org.baddev.common.schedulling.task.AbstractTask;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by IPotapchuk on 10/20/2016.
 */
public interface ScheduledTaskManager extends Serializable {
    void schedule(AbstractTask task, String cron);
    void execute(AbstractTask task);
    void terminate(Long taskId);
    void terminateAll();
    int getActiveCount();
    boolean isScheduled(Long taskId);
    Collection<AbstractTask> getScheduledTasks();
    Optional<ScheduledFuture> getFuture(Long taskId);
}
