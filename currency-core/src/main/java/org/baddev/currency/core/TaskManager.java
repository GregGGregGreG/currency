package org.baddev.currency.core;

import org.baddev.currency.core.task.AbstractTask;
import org.springframework.scheduling.support.CronTrigger;

/**
 * Created by IPotapchuk on 10/20/2016.
 */
public interface TaskManager {
    void schedule(AbstractTask task, CronTrigger trigger);
    void execute(AbstractTask task);
    void terminate(Long taskId);
    void terminateAll();
    int getActiveCount();
}
