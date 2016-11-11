package org.baddev.common.schedulling.task;

import org.baddev.common.event.BaseDataEvent;
import org.baddev.common.event.EventPublisher;
import org.baddev.common.utils.AssertUtils;

/**
 * Created by IPotapchuk on 10/7/2016.
 */
public abstract class AbstractCallbackTask<T extends BaseDataEvent> extends AbstractTask {

    private volatile EventPublisher eventPublisher;
    private volatile boolean notified;

    public void setEventPublisher(EventPublisher eventPublisher) {
        AssertUtils.notNull(eventPublisher);
        this.eventPublisher = eventPublisher;
    }

    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    @Override
    protected void doJob() {
        callback(beforeCallback());
    }

    protected abstract T beforeCallback();

    protected void callback(T event) {
        if (eventPublisher != null && !eventPublisher.getSubscribers().isEmpty()) {
            eventPublisher.publish(event);
            notified = true;
        }
    }

    public boolean isNotified() {
        return notified;
    }
}
