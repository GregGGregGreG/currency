package org.baddev.currency.mail.listener;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.baddev.common.event.GenericEventListener;
import org.baddev.common.mail.ApplicationMailer;
import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.springframework.beans.factory.annotation.Required;

/**
 * Created by IPotapchuk on 6/22/2016.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class MailExchangeCompletionListener implements GenericEventListener<ExchangeCompletionEvent> {

    private static final long serialVersionUID = 2586601749325354904L;

    private final ApplicationMailer mailer;

    @Setter(onMethod = @__({@Required}), onParam = @__({@NonNull}))
    private String email;

    @Override
    public void onEvent(ExchangeCompletionEvent e) {
        IExchangeOperation operation = e.getEventData();
        String exchInfo = String.format("Exchange task %d completed. %.2f %s <> %.2f %s.",
                operation.getId(),
                operation.getFromAmount(),
                operation.getFromCcy(),
                operation.getToAmount(),
                operation.getToCcy());
        mailer.sendMail(email, "Currency Exchange Task Completion", exchInfo);
    }
}
