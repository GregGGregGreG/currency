package org.baddev.currency.mail;

import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.core.event.NotificationEvent;
import org.baddev.currency.core.listener.NotificationListener;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by IPotapchuk on 6/22/2016.
 */
@Component
public class ExchangeCompletionMailer implements NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(ExchangeCompletionMailer.class);

    @Autowired
    private MailSender sender;
    @Autowired
    private SimpleMailMessage template;
    @Resource(name="mailerPool")
    private ThreadPoolTaskExecutor pool;

    @Override
    public <T extends NotificationEvent> void onNotificationEventReceived(T e) {
        if (e instanceof ExchangeCompletionEvent) {
            pool.execute(() -> {
                ExchangeOperation operation = ((ExchangeCompletionEvent) e).getEventData();
                String exchInfo = String.format("Exchange task %d completed. %.2f %s <> %.2f %s.",
                        operation.getId(),
                        operation.getFromAmount(),
                        operation.getFromCcy(),
                        operation.getToAmount(),
                        operation.getToCcy());
                SimpleMailMessage msg = new SimpleMailMessage(template);
                msg.setTo("ilya_potapchuk@mail.ru");
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
}
