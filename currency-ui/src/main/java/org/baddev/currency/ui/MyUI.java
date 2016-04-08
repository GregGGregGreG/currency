package org.baddev.currency.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.UI;
import org.baddev.currency.core.exchange.job.Exchanger;
import org.baddev.currency.core.fetcher.ExchangeRateFetcher;
import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.dao.exchange.ExchangeOperationDao;
import org.baddev.currency.fetcher.impl.nbu.NBU;
import org.baddev.currency.ui.view.RatesView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

@Theme("mytheme")
@Widgetset("org.baddev.currency.ui.MyAppWidgetset")
@SpringUI
public class MyUI extends UI {

    @NBU
    private ExchangeRateFetcher<BaseExchangeRate> fetcher;
    @Autowired
    private Exchanger exchanger;
    @Autowired
    private SpringViewProvider viewProvider;
    @Autowired
    private ExchangeOperationDao exchangeDao;

    public ExchangeRateFetcher<BaseExchangeRate> fetcher() {
        return fetcher;
    }

    public Exchanger exchanger() {
        return exchanger;
    }

    public ExchangeOperationDao exchangeDao() {
        return exchangeDao;
    }

    public static MyUI current(){
        return (MyUI)UI.getCurrent();
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
