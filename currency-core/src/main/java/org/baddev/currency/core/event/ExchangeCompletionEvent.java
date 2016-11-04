package org.baddev.currency.core.event;


import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public class ExchangeCompletionEvent extends BaseDataEvent<IExchangeOperation> {

    private boolean success;

    public ExchangeCompletionEvent(Object source, IExchangeOperation eventData, boolean success) {
        super(source, eventData);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
