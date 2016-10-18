package org.baddev.currency.ui.config;

import com.google.common.eventbus.EventBus;
import com.vaadin.server.ErrorEvent;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.annotation.VaadinSessionScope;
import com.vaadin.ui.UI;
import org.baddev.currency.core.event.Notifier;
import org.baddev.currency.core.event.NotifierImpl;
import org.baddev.currency.exchanger.ExchangerService;
import org.baddev.currency.fetcher.FetcherConfig;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.fetcher.service.ExchangeRateService;
import org.baddev.currency.mail.MailExchangeCompletionListener;
import org.baddev.currency.scheduler.exchange.task.NotifiableExchangeTask;
import org.baddev.currency.security.SecurityConfig;
import org.baddev.currency.ui.listener.UIExchangeCompletionListener;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
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
    @VaadinSessionScope
    Notifier notifier(){
        return new NotifierImpl();
    }

    @Bean
    @VaadinSessionScope
    MailExchangeCompletionListener mailer(MailSender sender, SimpleMailMessage template, ThreadPoolTaskExecutor mailerPool) {
        return new MailExchangeCompletionListener(sender, template, mailerPool);
    }

    @Bean
    @UIScope
    UIExchangeCompletionListener uiExchangeCompletionListener(Iso4217CcyService iso4217CcyService){
        return new UIExchangeCompletionListener(iso4217CcyService);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    NotifiableExchangeTask exchangeTask(Notifier notifier, ExchangeRateService exchangeRateService, ExchangerService exchangerService){
        return new NotifiableExchangeTask(notifier, exchangerService, exchangeRateService);
    }

    @Bean
    @UIScope
    EventBus bus() {
        return new EventBus((exception, context) -> {
            UI.getCurrent().getErrorHandler().error(new ErrorEvent(exception));
        });
    }

}