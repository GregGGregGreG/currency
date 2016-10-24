package org.baddev.currency.core.task;

/**
 * Created by IPotapchuk on 10/7/2016.
 */
public abstract class AbstractTask implements Runnable {

    private Long id;
    private boolean running;
    private boolean done;

    @Override
    public void run() {
        running = true;
        doJob();
        done = true;
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id){
        this.id = id;
    }

    protected abstract void doJob();

    public boolean isRunning() {
        return running && !done;
    }

    public boolean isDone() {
        return done;
    }
}
