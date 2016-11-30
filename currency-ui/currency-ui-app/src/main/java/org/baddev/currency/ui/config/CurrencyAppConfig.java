package org.baddev.currency.ui.config;

import com.google.common.eventbus.EventBus;
import com.vaadin.server.ErrorEvent;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.EnableVaadinNavigation;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.annotation.VaadinSessionScope;
import com.vaadin.ui.UI;
import org.baddev.common.ErrorHandlerAware;
import org.baddev.common.event.EventPublisher;
import org.baddev.common.event.impl.SynchronizedEventPublisher;
import org.baddev.common.mail.ApplicationMailer;
import org.baddev.common.schedulling.ScheduledTaskManager;
import org.baddev.currency.core.api.ExchangeRateService;
import org.baddev.currency.core.api.ExchangerService;
import org.baddev.currency.core.security.SecurityConfig;
import org.baddev.currency.exchanger.task.NotifiableExchangeTask;
import org.baddev.currency.fetcher.FetcherConfig;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.jooq.DataAccessConfig;
import org.baddev.currency.mail.MailConfig;
import org.baddev.currency.mail.listener.MailExchangeCompletionListener;
import org.baddev.currency.scheduler.ScheduledTaskManagerImpl;
import org.baddev.currency.scheduler.SchedulerConfig;
import org.baddev.currency.ui.UIErrorHandler;
import org.baddev.currency.ui.listener.UIExchangeCompletionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static com.vaadin.spring.internal.VaadinSessionScope.VAADIN_SESSION_SCOPE_NAME;
import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;

@Configuration
@ComponentScan(basePackages = {
        "org.baddev.currency.ui",
        "org.baddev.currency.exchanger",
        "org.baddev.currency.security.user"})
@Import({
        DataAccessConfig.class,
        FetcherConfig.class,
        MailConfig.class,
        SecurityConfig.class,
        SchedulerConfig.class
})
@EnableVaadin
@EnableVaadinNavigation
@PropertySource("classpath:currency_app.properties")
public class CurrencyAppConfig {

    @Bean
    @VaadinSessionScope
    EventPublisher eventPublisher(Logger log) {
        SynchronizedEventPublisher publisher = new SynchronizedEventPublisher(log);
        publisher.setErrorHandler(new UIErrorHandler());
        return new SynchronizedEventPublisher(log);
    }

    @Bean
    @Scope(value = VAADIN_SESSION_SCOPE_NAME, proxyMode = INTERFACES)
    @Primary
    ScheduledTaskManager taskManager(ThreadPoolTaskScheduler scheduler, Logger log) {
        return new ScheduledTaskManagerImpl(log, scheduler);
    }

    @Bean
    @VaadinSessionScope
    MailExchangeCompletionListener mailListener(ApplicationMailer mailer) {
        if(mailer instanceof ErrorHandlerAware)
            ((ErrorHandlerAware) mailer).setErrorHandler(new UIErrorHandler());
        return new MailExchangeCompletionListener(mailer);
    }

    @Bean
    @UIScope
    UIExchangeCompletionListener uiExchangeCompletionListener(Iso4217CcyService iso4217CcyService) {
        return new UIExchangeCompletionListener(iso4217CcyService);
    }

    @Bean
    @UIScope
    EventBus bus() {
        return new EventBus((exception, context) -> UI.getCurrent().getErrorHandler().error(new ErrorEvent(exception)));
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    NotifiableExchangeTask exchangeTask(ExchangeRateService exchangeRateService,
                                        ExchangerService exchangerService,
                                        EventPublisher publisher) {
        NotifiableExchangeTask task = new NotifiableExchangeTask(exchangerService, exchangeRateService);
        task.setErrorHandler(new UIErrorHandler());
        task.setEventPublisher(publisher);
        return task;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Logger logger(InjectionPoint injectionPoint){
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
    }

}