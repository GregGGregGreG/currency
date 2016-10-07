package org.baddev.currency.scheduler.exchange.task;

import org.baddev.currency.core.event.NotificationEvent;
import org.baddev.currency.core.event.Notifier;

/**
 * Created by IPotapchuk on 10/7/2016.
 */
public abstract class AbstractCallbackTask<T extends NotificationEvent> extends AbstractTask {

    private Notifier notifier;
    private boolean notified;

    public AbstractCallbackTask(Notifier n) {
        notifier = n;
    }

    @Override
    protected void doJob() {
        callback(beforeCallback());
    }

    protected abstract T beforeCallback();

    protected void callback(T event) {
        if (!notifier.getSubscribers().isEmpty()) {
            notifier.doNotify(event);
            notified = true;
        }
    }

    public boolean isNotified() {
        return notified;
    }
}
