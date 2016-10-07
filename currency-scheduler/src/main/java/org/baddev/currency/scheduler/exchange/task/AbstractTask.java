package org.baddev.currency.scheduler.exchange.task;

/**
 * Created by IPotapchuk on 10/7/2016.
 */
public abstract class AbstractTask implements Runnable {

    private boolean running;
    private boolean done;

    @Override
    public void run() {
        running = true;
        doJob();
        done = true;
    }

    protected abstract void doJob();

    public boolean isRunning() {
        return running && !done;
    }

    public boolean isDone() {
        return done;
    }
}
