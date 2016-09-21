package org.baddev.currency.ui.component.base;

import com.google.common.eventbus.EventBus;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import org.baddev.currency.core.RoleEnum;
import org.baddev.currency.security.utils.SecurityUtils;
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

import static org.baddev.currency.ui.CurrencyUI.currencyUI;

/**
 * Created by IPotapchuk on 5/16/2016.
 */
public abstract class AbstractCcyView extends VerticalLayout implements View, InitializingBean, DisposableBean, BeanFactoryAware {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private   SettingsWindow settingsWindow;
    protected EventBus       bus;
    protected BeanFactory    beanFactory;

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
        addComponent(menuBar());
        VerticalLayout rootLayout = contentRoot();
        addComponent(rootLayout);
        setExpandRatio(rootLayout, 1.0f);
        postInit(rootLayout);
        setSizeFull();
        log.debug("View created {}, {}", getClass().getName(), hashCode());
    }

    private VerticalLayout contentRoot() {
        VerticalLayout root = new VerticalLayout();
        init(root);
        root.setSizeFull();
        return root;
    }

    protected void init(VerticalLayout rootLayout) {
    }

    protected void postInit(VerticalLayout rootLayout) {
    }

    protected void customizeMenuBar(MenuBar menuBar) {
    }

    private MenuBar menuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        menuBar.addStyleName("small");
        String loggedIn = SecurityUtils.loggedInUserName();
        if (!StringUtils.isEmpty(loggedIn.trim())) {
            MenuBar.MenuItem parent = menuBar.addItem(loggedIn, FontAwesome.USER, null);
            if (SecurityUtils.hasAnyRole(RoleEnum.ADMIN)) {
                parent.addItem("Users", FontAwesome.USERS, item -> navigateTo(UsersView.NAME));
            }
            parent.addItem("Settings", FontAwesome.GEAR, selectedItem -> currencyUI().addWindow(settingsWindow));
            parent.addItem("Logout", FontAwesome.SIGN_OUT, selectedItem -> bus.post(new LogoutEvent(this)));
        }
        customizeMenuBar(menuBar);
        return menuBar;
    }

    protected static void navigateTo(String viewName) {
        currencyUI().getNavigator().navigateTo(viewName);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

    @Override
    public void destroy() {
        log.debug("View destroyed {}", getClass().getName(), hashCode());
    }

}
