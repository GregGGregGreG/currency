package org.baddev.currency.mail.listener;

import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.core.event.NotificationListener;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.mail.Mailer;
import org.springframework.beans.factory.annotation.Required;

/**
 * Created by IPotapchuk on 6/22/2016.
 */
public class MailExchangeCompletionListener implements NotificationListener<ExchangeCompletionEvent> {

    private static final long serialVersionUID = 2586601749325354904L;

    private Mailer mailer;
    private String email;

    public MailExchangeCompletionListener(Mailer mailer) {
        this.mailer = mailer;
    }

    @Required
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void notificationReceived(ExchangeCompletionEvent e) {
        IExchangeOperation operation = e.getEventData();
        String exchInfo = String.format("Exchange task %d completed. %.2f %s <> %.2f %s.",
                operation.getId(),
                operation.getFromAmount(),
                operation.getFromCcy(),
                operation.getToAmount(),
                operation.getToCcy());
        mailer.sendSimpleMail(email, "Currency Exchange Task Completion", exchInfo);
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
