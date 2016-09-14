package org.baddev.currency.ui.config;

import com.google.common.eventbus.EventBus;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.UIScope;
import org.baddev.currency.fetcher.FetcherConfig;
import org.baddev.currency.mail.ExchangeCompletionMailer;
import org.baddev.currency.security.SecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableVaadin
@Import({FetcherConfig.class, SecurityConfig.class})
@ImportResource(locations = {
        "classpath:jooq-applicationContext.xml",
        "classpath:exchanger-applicationContext.xml",
        "classpath:scheduler-applicationContext.xml",
        "classpath:mail-applicationContext.xml"
})
public class ApplicationConfig {

    @Bean
    @UIScope
    EventBus bus() {
        return new EventBus();
    }

    @Bean
    @UIScope
    ExchangeCompletionMailer mailer(MailSender sender, SimpleMailMessage template, ThreadPoolTaskExecutor mailerPool){
        return new ExchangeCompletionMailer(sender, template, mailerPool);
    }

}