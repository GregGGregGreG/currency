package org.baddev.currency.ui.config;

import com.google.common.eventbus.EventBus;
import com.vaadin.server.ErrorEvent;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import org.baddev.currency.core.TaskManager;
import org.baddev.currency.core.api.ExchangeRateService;
import org.baddev.currency.core.api.ExchangerService;
import org.baddev.currency.core.event.Notifier;
import org.baddev.currency.core.event.NotifierImpl;
import org.baddev.currency.core.task.NotifiableExchangeTask;
import org.baddev.currency.fetcher.FetcherConfig;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.mail.Mailer;
import org.baddev.currency.mail.listener.MailExchangeCompletionListener;
import org.baddev.currency.scheduler.TaskManagerImpl;
import org.baddev.currency.security.SecurityConfig;
import org.baddev.currency.ui.listener.UIExchangeCompletionListener;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static com.vaadin.spring.internal.VaadinSessionScope.VAADIN_SESSION_SCOPE_NAME;
import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;

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
    @Scope(value = VAADIN_SESSION_SCOPE_NAME)
    Notifier uiNotifier() {
        return new NotifierImpl();
    }

    @Bean
    @Scope(value = VAADIN_SESSION_SCOPE_NAME, proxyMode = INTERFACES)
    @Primary
    TaskManager taskManager(ThreadPoolTaskScheduler scheduler) {
        return new TaskManagerImpl(scheduler);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    MailExchangeCompletionListener mailListener(Mailer mailer) {
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
        return new EventBus((exception, context) -> {
            UI.getCurrent().getErrorHandler().error(new ErrorEvent(exception));
        });
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    NotifiableExchangeTask exchangeTask(Notifier uiNotifier,
                                        ExchangeRateService exchangeRateService,
                                        ExchangerService exchangerService) {
        NotifiableExchangeTask task = new NotifiableExchangeTask(exchangerService, exchangeRateService);
        task.setNotifier(uiNotifier);
        return task;
    }

}