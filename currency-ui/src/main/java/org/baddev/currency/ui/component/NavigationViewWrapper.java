package org.baddev.currency.ui.component;

import com.google.common.eventbus.EventBus;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import org.baddev.currency.core.RoleEnum;
import org.baddev.currency.security.utils.SecurityUtils;
import org.baddev.currency.ui.component.view.UsersView;
import org.baddev.currency.ui.component.view.base.AbstractCcyView;
import org.baddev.currency.ui.component.window.SettingsWindow;
import org.baddev.currency.ui.security.event.LogoutEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import static org.baddev.currency.ui.CurrencyUI.currencyUI;
import static org.baddev.currency.ui.util.UIUtils.navigate;

/**
 * Created by IPotapchuk on 9/27/2016.
 */
@SpringComponent
@UIScope
public class NavigationViewWrapper extends AbstractCcyView implements ViewDisplay {

    private AbstractCcyView currentView;
    private MenuBar menuBar;
    @Autowired
    private SettingsWindow settingsWindow;
    @Autowired
    private EventBus bus;

    @Override
    protected void init(VerticalLayout rootLayout) {
        menuBar = new MenuBar();
        menuBar.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        menuBar.addStyleName("small");
        addComponent(menuBar);
    }

    @Override
    public void showView(View view) {
        VerticalLayout root = (VerticalLayout) getComponent(1); //obtaining root
        if (currentView != null) root.removeComponent(currentView);
        if (view instanceof AbstractCcyView) currentView = (AbstractCcyView) view;
        setupMenuBar();
        root.addComponent(currentView);
    }

    private void setupMenuBar() {
        menuBar.removeItems();
        String loggedIn = SecurityUtils.loggedInUserName();
        if (!StringUtils.isEmpty(loggedIn.trim())) {
            MenuBar.MenuItem parent = menuBar.addItem(loggedIn, FontAwesome.USER, null);
            if (SecurityUtils.hasAnyRole(RoleEnum.ADMIN)) {
                parent.addItem("Users", FontAwesome.USERS, item -> navigate(UsersView.NAME));
            }
            parent.addItem("Settings", FontAwesome.GEAR, selectedItem -> currencyUI().addWindow(settingsWindow));
            parent.addItem("Logout", FontAwesome.SIGN_OUT, selectedItem -> bus.post(new LogoutEvent(this)));
        }
        if (currentView != null) currentView.customizeMenuBar(menuBar);
    }
}
