package org.baddev.currency.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by IPotapchuk on 10/24/2016.
 */
public class Mailer {

    private static final Logger log = LoggerFactory.getLogger(Mailer.class);

    private MailSender             sender;
    private SimpleMailMessage      template;
    private ThreadPoolTaskExecutor pool;

    @Required
    public void setSender(MailSender sender) {
        this.sender = sender;
    }

    @Required
    public void setTemplate(SimpleMailMessage template) {
        this.template = template;
    }

    @Required
    public void setPool(ThreadPoolTaskExecutor pool) {
        this.pool = pool;
    }

    public void sendSimpleMail(String to, String subject, String txt){
        pool.execute(() -> {
            SimpleMailMessage msg = new SimpleMailMessage(template);
            msg.setTo(to);
            msg.setSentDate(new Date());
            msg.setSubject(subject);
            msg.setText(txt);
            try {
                sender.send(msg);
            } catch (MailException ex) {
                log.error("Failed to send email", ex);
                throw ex;
            }
        });
    }

    public void sendSimpleMails(String subject, String txt, String[]tos){
        Arrays.stream(tos).forEach(to -> sendSimpleMail(to, subject, txt));
    }

}
