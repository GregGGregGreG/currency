package org.baddev.currency.scheduler;

import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
public interface CcyTaskScheduler<T extends IExchangeTask> {

    Long schedule(T taskData);

    void execute(T taskData);

    boolean cancel(Long id, boolean remove);

    void cancelAll(boolean remove);

    int getActiveCount();

}
