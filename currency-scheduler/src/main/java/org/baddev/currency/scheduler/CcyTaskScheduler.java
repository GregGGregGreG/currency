package org.baddev.currency.scheduler;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
public interface CcyTaskScheduler<T> {

    Long schedule(T taskData);

    void execute(T taskData);

    boolean cancel(Long id, boolean remove);

    void cancelAll(boolean remove);

    int getActiveCount();

}
