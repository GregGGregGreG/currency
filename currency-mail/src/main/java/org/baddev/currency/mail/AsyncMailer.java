package org.baddev.currency.mail;

import org.baddev.currency.core.ApplicationMailer;
import org.baddev.currency.core.meta.Prod;
import org.baddev.currency.core.util.Safe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by IPotapchuk on 10/24/2016.
 */
@Component
@Prod
@Primary
public class AsyncMailer implements ApplicationMailer {

    private static final Logger log = LoggerFactory.getLogger(AsyncMailer.class);

    @Autowired
    private MailSender sender;
    @Autowired
    private ThreadPoolTaskExecutor pool;
    @Autowired
    private ObjectProvider<SimpleMailMessage> msgProvider;

    @Override
    public void sendMail(String to, String subject, String txt) {
        pool.execute(() -> {
            Safe.tryCall(() -> {
                SimpleMailMessage msg = msgProvider.getIfAvailable();
                msg.setTo(to);
                msg.setSentDate(new Date());
                msg.setSubject(subject);
                msg.setText(txt);
                sender.send(msg);
                log.debug("Mail sent");
            });
        });
    }

}
