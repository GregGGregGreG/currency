package org.baddev.currency.ui.listener;

import com.vaadin.ui.UI;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.baddev.common.event.GenericEventListener;
import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.ui.util.FormatUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import static org.baddev.currency.ui.util.NotificationUtils.notifyTray;

/**
 * Created by Ilya on 18.10.2016.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class UIExchangeCompletionListener implements GenericEventListener<ExchangeCompletionEvent> {

    private static final long serialVersionUID = -4068324837833281874L;

    private final Iso4217CcyService ccyService;

    @Setter(onMethod = @__({@Required}))
    private UI ui;

    @Override
    public void onEvent(ExchangeCompletionEvent event) {
        IExchangeOperation operation = event.getEventData();
        Assert.notNull(event.getEventData(), "eventData can't be null");
        ui.access(() -> {
            String fromCcyNames = FormatUtils.joinByComma(
                    ccyService.findCcyNamesByCode(operation.getFromCcy())
            );
            String toCcyNames = FormatUtils.joinByComma(
                    ccyService.findCcyNamesByCode(operation.getToCcy())
            );
            notifyTray("Exchange Task Completion", FormatUtils.exchangeNotification(
                    operation.getFromAmount(),
                    fromCcyNames,
                    operation.getFromCcy(),
                    operation.getToAmount(),
                    toCcyNames,
                    operation.getToCcy()));
        });
    }

}
