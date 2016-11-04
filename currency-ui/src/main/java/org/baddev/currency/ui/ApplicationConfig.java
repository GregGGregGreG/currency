package org.baddev.currency.ui;

import com.google.common.eventbus.EventBus;
import com.vaadin.server.ErrorEvent;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.annotation.VaadinSessionScope;
import com.vaadin.ui.UI;
import org.baddev.currency.core.ApplicationMailer;
import org.baddev.currency.core.api.ExchangeRateService;
import org.baddev.currency.core.api.ExchangerService;
import org.baddev.currency.core.event.EventPublisher;
import org.baddev.currency.core.event.impl.SynchronizedEventPublisher;
import org.baddev.currency.core.task.NotifiableExchangeTask;
import org.baddev.currency.core.task.TaskManager;
import org.baddev.currency.fetcher.FetcherConfig;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.jooq.DataAccessConfig;
import org.baddev.currency.mail.MailConfig;
import org.baddev.currency.mail.listener.MailExchangeCompletionListener;
import org.baddev.currency.scheduler.SchedulerConfig;
import org.baddev.currency.scheduler.TaskManagerImpl;
import org.baddev.currency.security.SecurityConfig;
import org.baddev.currency.ui.listener.UIExchangeCompletionListener;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static com.vaadin.spring.internal.VaadinSessionScope.VAADIN_SESSION_SCOPE_NAME;
import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;

@Configuration
@PropertySource("classpath:currency_app.properties")
@EnableVaadin
@Import({
        DataAccessConfig.class,
        FetcherConfig.class,
        MailConfig.class,
        SecurityConfig.class,
        SchedulerConfig.class
})
public class ApplicationConfig {

    @Bean
    @VaadinSessionScope
    EventPublisher eventPublisher() {
        return new SynchronizedEventPublisher();
    }

    @Bean
    @Scope(value = VAADIN_SESSION_SCOPE_NAME, proxyMode = INTERFACES)
    @Primary
    TaskManager taskManager(ThreadPoolTaskScheduler scheduler) {
        return new TaskManagerImpl(scheduler);
    }

    @Bean
    @VaadinSessionScope
    MailExchangeCompletionListener mailListener(ApplicationMailer mailer) {
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
                                        ExchangerService exchangerService) {
        return new NotifiableExchangeTask(exchangerService, exchangeRateService);
    }

}