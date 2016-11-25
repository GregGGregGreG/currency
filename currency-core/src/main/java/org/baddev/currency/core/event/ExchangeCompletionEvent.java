package org.baddev.currency.core.event;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.baddev.common.event.BaseDataEvent;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;

import java.util.UUID;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExchangeCompletionEvent extends BaseDataEvent<IExchangeOperation> {

    public ExchangeCompletionEvent(UUID uuid, Object source, IExchangeOperation eventData) {
        super(uuid, source, eventData);
    }

    public ExchangeCompletionEvent(Object source, IExchangeOperation eventData) {
        super(source, eventData);
    }
}
