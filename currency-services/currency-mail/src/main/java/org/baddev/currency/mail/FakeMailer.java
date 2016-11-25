package org.baddev.currency.mail;

import lombok.RequiredArgsConstructor;
import org.baddev.common.CommonErrorHandler;
import org.baddev.common.mail.ApplicationMailer;
import org.baddev.currency.core.meta.Dev;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Created by IPotapchuk on 10/31/2016.
 */
@Component
@Dev
@RequiredArgsConstructor
public class FakeMailer implements ApplicationMailer {

    private final Logger log;

    @Override
    public void sendMail(String to, String subject, String content) {
        log.info("Mail sent, to={}, subj={}, content={}", to, subject, content);
    }

    @Override
    public void setErrorHandler(CommonErrorHandler errorHandler) {
    }
}
