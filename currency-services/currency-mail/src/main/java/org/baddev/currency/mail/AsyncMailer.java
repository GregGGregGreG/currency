package org.baddev.currency.mail;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.baddev.common.CommonErrorHandler;
import org.baddev.common.ErrorHandlerAware;
import org.baddev.common.mail.ApplicationMailer;
import org.baddev.common.utils.Safe;
import org.baddev.currency.core.meta.Prod;
import org.slf4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
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
@RequiredArgsConstructor
public class AsyncMailer implements ApplicationMailer, ErrorHandlerAware {

    private final Logger                            log;
    private final MailSender                        sender;
    private final ThreadPoolTaskExecutor            pool;
    private final ObjectProvider<SimpleMailMessage> msgProvider;
    private       CommonErrorHandler                errorHandler;

    @Override
    public void sendMail(@NonNull String to, @NonNull String subject, @NonNull String txt) {
        pool.execute(() -> {
            Safe.tryCall(errorHandler, () -> {
                SimpleMailMessage msg = msgProvider.getIfAvailable();
                msg.setTo(to);
                msg.setSentDate(new Date());
                msg.setSubject(subject);
                msg.setText(txt);
                sender.send(msg);
                log.debug("Mail sent to \"{}\"", to);
            });
        });
    }

    @Override
    public void setErrorHandler(CommonErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
}
