package org.baddev.common.schedulling.task;

import org.baddev.common.CommonErrorHandler;
import org.baddev.common.utils.AssertUtils;

/**
 * Created by IPotapchuk on 10/7/2016.
 */
public abstract class AbstractTask implements Runnable {

    private Long               id;
    private boolean            running;
    private boolean            done;
    private CommonErrorHandler errorHandler;

    protected void setId(Long id) {
        AssertUtils.notNull(id, "id must be a non-null value");
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setErrorHandler(CommonErrorHandler errorHandler) {
        AssertUtils.notNull(errorHandler, "errorHandler must be a non-null value");
        this.errorHandler = errorHandler;
    }

    public boolean isRunning() {
        return running && !done;
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public void run() {
        running = true;
        try {
            doJob();
        } catch (Exception e) {
            done = false;
            if (errorHandler != null) errorHandler.handle(e);
            else throw e;
        }
        done = true;
    }

    protected abstract void doJob();
}
