package org.baddev.currency.ui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.baddev.currency.core.exchange.entity.ExchangeOperation;
import org.baddev.currency.core.exchange.job.Exchanger;
import org.baddev.currency.core.fetcher.ExchangeRateFetcher;
import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.dao.exchange.ExchangeOperationDao;
import org.baddev.currency.dao.fetcher.ExchangeRateDao;
import org.baddev.currency.fetcher.impl.nbu.NBU;
import org.baddev.currency.notifier.Notifier;
import org.baddev.currency.notifier.event.ExchangeCompletionEvent;
import org.baddev.currency.notifier.event.NotificationEvent;
import org.baddev.currency.notifier.listener.NotificationListener;
import org.baddev.currency.scheduler.ScheduledExchangeManager;
import org.baddev.currency.ui.component.RatesView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

@Theme("mytheme")
@Widgetset("org.baddev.currency.ui.MyAppWidgetset")
@SpringUI
@Push
public class MyUI extends UI implements NotificationListener{

    @Autowired
    private SpringViewProvider viewProvider;
    @NBU
    private ExchangeRateFetcher<BaseExchangeRate> fetcher;
    @Autowired
    private Exchanger exchanger;
    @Autowired
    private ExchangeOperationDao exchangeDao;
    @Autowired
    private ExchangeRateDao rateDao;
    @Autowired
    private ScheduledExchangeManager scheduler;
    @Autowired
    private Notifier notifier;

    public ExchangeRateFetcher<BaseExchangeRate> fetcher() {
        return fetcher;
    }

    public Exchanger exchanger() {
        return exchanger;
    }

    public ExchangeOperationDao exchangeDao() {
        return exchangeDao;
    }

    public ExchangeRateDao rateDao() {
        return rateDao;
    }

    public ScheduledExchangeManager scheduler() {
        return scheduler;
    }

    @Override
    public <T extends NotificationEvent> void onNotificationReceived(T e) {
        if (e instanceof ExchangeCompletionEvent) {
            ExchangeOperation operation = ((ExchangeCompletionEvent) e).getEventData();
            //push notification to UI
            access(() -> {
                String exchInfo = String.format("Exchange task %d completed. %.2f %s -> %.2f %s.",
                        operation.getId(),
                        operation.getAmount(),
                        operation.getAmountCurrencyCode(),
                        operation.getExchangedAmount(),
                        operation.getExchangedAmountCurrencyCode());
                Notification.show("Exchange task completion", exchInfo, Notification.Type.TRAY_NOTIFICATION);
            });
        }
    }

    public static MyUI myUI() {
        return (MyUI) UI.getCurrent();
    }

    public void registerListener(NotificationListener l) {
        notifier.subscribe(l);
    }

    public void unregisterListener(NotificationListener l) {
        notifier.unsubscribe(l);
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Navigator navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);
        navigator.navigateTo(RatesView.NAME);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends SpringVaadinServlet {
    }

    @WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    }

    @Configuration
    @EnableVaadin
    public static class MyConfiguration {
    }
}
