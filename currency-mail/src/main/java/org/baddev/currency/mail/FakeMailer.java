package org.baddev.currency.mail;

import org.baddev.currency.core.ApplicationMailer;
import org.baddev.currency.core.meta.Dev;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by IPotapchuk on 10/31/2016.
 */
@Component
@Dev
public class FakeMailer implements ApplicationMailer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationMailer.class);

    @Override
    public void sendMail(String to, String subject, String content) {
        log.info("Mail sent, to={}, subj={}, content={}", to, subject, content);
    }
}
