package org.baddev.currency.ui.component.base;

import com.google.common.eventbus.EventBus;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import org.baddev.currency.core.RoleEnum;
import org.baddev.currency.security.utils.SecurityCtxHelper;
import org.baddev.currency.ui.component.view.UsersView;
import org.baddev.currency.ui.component.window.SettingsWindow;
import org.baddev.currency.ui.security.event.LogoutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.util.Arrays;

import static org.baddev.currency.ui.CurrencyUI.currencyUI;

/**
 * Created by IPotapchuk on 5/16/2016.
 */
public abstract class AbstractCcyView extends VerticalLayout implements View, InitializingBean, DisposableBean, BeanFactoryAware {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private SettingsWindow settingsWindow;
    protected EventBus bus;
    protected BeanFactory beanFactory;

    public AbstractCcyView(SettingsWindow settingsWindow, EventBus bus) {
        this.settingsWindow = settingsWindow;
        this.bus = bus;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setSizeFull();
        addComponent(contentRoot());
        init();
        log.debug("View created {}, {}", getClass().getName(), hashCode());
    }

    protected void init() {
    }

    protected VerticalLayout contentRoot() {
        VerticalLayout content = new VerticalLayout();
        content.addComponent(menuBar());
        content.setSizeFull();
        return content;
    }

    private MenuBar menuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setWidth(100.0f, Unit.PERCENTAGE);
        menuBar.addStyleName("small");
        String loggedIn = SecurityCtxHelper.loggedInUserName();
        if (!StringUtils.isEmpty(loggedIn.trim())) {
            MenuBar.MenuItem parent = menuBar.addItem(loggedIn, FontAwesome.USER, null);
            if (SecurityCtxHelper.hasAnyRole(RoleEnum.ADMIN)) {
                parent.addItem("Users", FontAwesome.USERS, item -> navigateTo(UsersView.NAME));
            }
            parent.addItem("Settings", FontAwesome.GEAR, selectedItem -> currencyUI().addWindow(settingsWindow));
            parent.addItem("Logout", FontAwesome.SIGN_OUT, selectedItem -> bus.post(new LogoutEvent(this)));
        }
        customizeMenuBar(menuBar);
        return menuBar;
    }

    protected abstract void customizeMenuBar(MenuBar menuBar);

    protected static void navigateTo(String viewName) {
        currencyUI().getNavigator().navigateTo(viewName);
    }

    public static void attachComponents(AbstractOrderedLayout l, Component... cs) {
        Arrays.stream(cs).forEach(c -> {
            if (l.getComponentIndex(c) == -1)
                l.addComponent(c);
        });
    }

    public static void toggleVisible(boolean visible, Component... components) {
        Arrays.stream(components).forEach(c -> c.setVisible(visible));
    }

    public static void toggleEnabled(boolean enabled, Component... components) {
        Arrays.stream(components).forEach(c -> c.setEnabled(enabled));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    @Override
    public void destroy() {
        log.debug("View destroyed {}", getClass().getName(), hashCode());
    }

}
