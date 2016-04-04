package org.baddev.currency.ui;


import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import org.baddev.currency.ui.currency.fetcher.ExchangeRateFetcher;
import org.baddev.currency.ui.currency.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.ui.currency.fetcher.entity.ExchangeRate;
import org.baddev.currency.ui.currency.fetcher.impl.nbu.NBU;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

@Theme("mytheme")
@Widgetset("org.baddev.currency.ui.MyAppWidgetset")
@SpringUI
public class MyUI extends UI {

    @NBU
    private ExchangeRateFetcher fetcher;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Grid grid = new Grid();
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setContainerDataSource(new BeanItemContainer<>(BaseExchangeRate.class));
        grid.setColumnOrder("id", "baseLiterCode", "literCode", "exchangeDate", "rate");
        ((BeanItemContainer<ExchangeRate>)grid.getContainerDataSource()).addAll(fetcher.fetchCurrent());
        setContent(grid);
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
