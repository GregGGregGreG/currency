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
import org.baddev.currency.notifier.NotifierService;
import org.baddev.currency.notifier.event.NotificationEvent;
import org.baddev.currency.notifier.listener.NotificationListener;
import org.baddev.currency.scheduler.ScheduledExchangeManager;
import org.baddev.currency.scheduler.event.ExchangeCompletionEvent;
import org.baddev.currency.ui.components.RatesView;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

@Theme("mytheme")
@Widgetset("org.baddev.currency.ui.MyAppWidgetset")
@SpringUI
@Push
public class MyUI extends UI implements NotificationListener, InitializingBean, ApplicationContextAware {

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

    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return ctx;
    }

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

    public static MyUI myUI() {
        return (MyUI) UI.getCurrent();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //subscribe to event publisher notifications
        Notifier n = ctx.getBean(NotifierService.class);
        n.subscribe(this);
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Navigator navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);
        navigator.navigateTo(RatesView.NAME);
    }

    //handle task's events
    @Override
    public void onNotify(NotificationEvent e) {
        if (e instanceof ExchangeCompletionEvent) {
            ExchangeOperation operation = ((ExchangeCompletionEvent) e).getEventData();
            //push notification to UI
            access(() -> Notification.show("Exchange task completion",
                    String.format("Exchanged amount: %.2f %s",
                            operation.getExchangedAmount(),
                            operation.getExchangedAmountCurrencyCode()),
                    Notification.Type.TRAY_NOTIFICATION)
            );
        }
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
