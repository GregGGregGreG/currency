package org.baddev.currency.ui.listener;

import com.vaadin.ui.UI;
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
public class UIExchangeCompletionListener implements GenericEventListener<ExchangeCompletionEvent> {

    private static final long serialVersionUID = -4068324837833281874L;

    private Iso4217CcyService ccyService;
    private UI ui;

    public UIExchangeCompletionListener(Iso4217CcyService ccyService) {
        Assert.notNull(ccyService, "ccyService can't be null");
        this.ccyService = ccyService;
    }

    public UIExchangeCompletionListener(Iso4217CcyService ccyService, UI ui) {
        this(ccyService);
        setUI(ui);
    }

    @Required
    public void setUI(UI ui) {
        Assert.notNull(ui, "ui can't be null");
        this.ui = ui;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIExchangeCompletionListener)) return false;

        UIExchangeCompletionListener that = (UIExchangeCompletionListener) o;

        return ui != null ? ui.equals(that.ui) : that.ui == null;

    }

    @Override
    public int hashCode() {
        return ui != null ? ui.hashCode() : 0;
    }
}
