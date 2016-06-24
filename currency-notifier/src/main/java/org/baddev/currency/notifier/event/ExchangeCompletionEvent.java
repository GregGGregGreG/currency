package org.baddev.currency.notifier.event;

import org.baddev.currency.core.exchanger.entity.ExchangeOperation;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public class ExchangeCompletionEvent extends BaseNotificationEvent<ExchangeOperation> {

    private boolean success;

    public ExchangeCompletionEvent(Object source, ExchangeOperation eventData, boolean success) {
        super(source, eventData);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
