package org.baddev.currency.core.task;

import org.baddev.currency.core.event.NotificationEvent;
import org.baddev.currency.core.event.Notifier;

/**
 * Created by IPotapchuk on 10/7/2016.
 */
public abstract class AbstractCallbackTask<T extends NotificationEvent> extends AbstractTask {

    private Notifier notifier;
    private volatile boolean notified;

    public void setNotifier(Notifier notifier) {
        this.notifier = notifier;
    }

    public Notifier getNotifier() {
        return notifier;
    }

    @Override
    protected void doJob() {
        callback(beforeCallback());
    }

    protected abstract T beforeCallback();

    protected void callback(T event) {
        if (notifier != null && !notifier.getSubscribers().isEmpty()) {
            notifier.doNotify(event);
            notified = true;
        }
    }

    public boolean isNotified() {
        return notified;
    }
}
