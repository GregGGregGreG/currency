package org.baddev.currency.mail.listener;

import org.baddev.currency.core.ApplicationMailer;
import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.core.event.GenericEventListener;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.springframework.beans.factory.annotation.Required;

/**
 * Created by IPotapchuk on 6/22/2016.
 */
public class MailExchangeCompletionListener implements GenericEventListener<ExchangeCompletionEvent> {

    private static final long serialVersionUID = 2586601749325354904L;

    private ApplicationMailer mailer;
    private String email;

    public MailExchangeCompletionListener(ApplicationMailer mailer) {
        this.mailer = mailer;
    }

    @Required
    public void setEmail(String email) {
        this.email = email;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MailExchangeCompletionListener)) return false;

        MailExchangeCompletionListener that = (MailExchangeCompletionListener) o;

        return email != null ? email.equals(that.email) : that.email == null;

    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}
