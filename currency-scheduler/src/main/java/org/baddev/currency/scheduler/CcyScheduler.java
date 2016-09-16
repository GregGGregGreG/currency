package org.baddev.currency.scheduler;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
public interface CcyScheduler<T, ID> {
    ID schedule(T taskData);
    void execute(T taskData);
    void cancel(ID id, boolean remove);
    void cancelAll(boolean remove);
    int getActiveCount();
}
