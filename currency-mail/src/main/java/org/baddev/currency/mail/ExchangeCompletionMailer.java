package org.baddev.currency.mail;

import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.core.listener.NotificationListener;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Date;

/**
 * Created by IPotapchuk on 6/22/2016.
 */
public class ExchangeCompletionMailer implements NotificationListener<ExchangeCompletionEvent> {

    private static final Logger log = LoggerFactory.getLogger(ExchangeCompletionMailer.class);

    private MailSender             sender;
    private SimpleMailMessage      template;
    private ThreadPoolTaskExecutor pool;
    private String                 email;

    public ExchangeCompletionMailer(MailSender sender,
                                    SimpleMailMessage template,
                                    ThreadPoolTaskExecutor pool) {
        this.pool = pool;
        this.template = template;
        this.sender = sender;
    }

    @Required
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void notificationReceived(ExchangeCompletionEvent e) {
        pool.execute(() -> {
            IExchangeOperation operation = e.getEventData();
            String exchInfo = String.format("Exchange task %d completed. %.2f %s <> %.2f %s.",
                    operation.getId(),
                    operation.getFromAmount(),
                    operation.getFromCcy(),
                    operation.getToAmount(),
                    operation.getToCcy());
            SimpleMailMessage msg = new SimpleMailMessage(template);
            msg.setTo(email);
            msg.setSentDate(new Date());
            msg.setSubject("Currency Exchange Task Completion");
            msg.setText(exchInfo);
            try {
                sender.send(msg);
            } catch (MailException ex) {
                log.error("Failed to send email", ex);
                throw ex;
            }
        });
    }

}
