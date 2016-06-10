package org.baddev.currency.scheduler;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
public interface CcyTaskScheduler<T> {

    Long schedule(T initData, String cron);

    void execute(T initData);

    boolean cancel(Long id);

    void cancelAll();

    int getActiveCount();

}
