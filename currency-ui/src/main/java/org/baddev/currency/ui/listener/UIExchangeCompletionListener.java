package org.baddev.currency.ui.listener;

import com.vaadin.ui.UI;
import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.core.event.NotificationListener;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.ui.util.FormatUtils;
import org.springframework.beans.factory.annotation.Required;

import static org.baddev.currency.ui.util.NotificationUtils.notifyTray;

/**
 * Created by Ilya on 18.10.2016.
 */
public class UIExchangeCompletionListener implements NotificationListener<ExchangeCompletionEvent> {

    private static final long serialVersionUID = -4068324837833281874L;

    private Iso4217CcyService ccyService;

    public UIExchangeCompletionListener() {
    }

    public UIExchangeCompletionListener(Iso4217CcyService ccyService) {
        this.ccyService = ccyService;
    }

    @Required
    public UIExchangeCompletionListener setCcyService(Iso4217CcyService ccyService) {
        this.ccyService = ccyService;
        return this;
    }

    @Override
    public void notificationReceived(ExchangeCompletionEvent e) {
        IExchangeOperation operation = e.getEventData();
        UI.getCurrent().access(() -> {
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
