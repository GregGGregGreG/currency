package org.baddev.currency.ui.component.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import org.baddev.currency.core.util.RoleEnum;
import org.baddev.currency.security.utils.SecurityUtils;
import org.baddev.currency.ui.component.view.base.AbstractCcyView;
import org.baddev.currency.ui.component.window.SettingsWindow;
import org.baddev.currency.ui.security.event.LogoutEvent;
import org.baddev.currency.ui.util.EventBus;
import org.baddev.currency.ui.util.Navigator;
import org.baddev.currency.ui.util.Styles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

import static org.baddev.currency.ui.CurrencyUI.get;

/**
 * Created by IPotapchuk on 9/27/2016.
 */
@SpringComponent
@UIScope
public class MainView extends VerticalLayout implements ViewDisplay {

    private AbstractCcyView currentView;
    private MenuBar menuBar;
    @Autowired
    private SettingsWindow settingsWindow;
    @Value("${app.title}")
    private String appTitle;

    @PostConstruct
    private void init() {
        menuBar = new MenuBar();
        menuBar.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        menuBar.addStyleName("small");
        addComponent(menuBar);
        setSizeFull();
    }

    @Override
    public void showView(View view) {
        if (currentView != null) removeComponent(currentView);
        if (view instanceof AbstractCcyView) currentView = (AbstractCcyView) view;
        setupMenuBar();
        addComponent(currentView);
        setExpandRatio(currentView, 1.0f);
    }

    private void setupMenuBar() {
        menuBar.removeItems();
        String loggedIn = SecurityUtils.loggedInUserName();
        if (!StringUtils.isEmpty(loggedIn.trim())) {
            MenuBar.MenuItem dropDownParent = menuBar.addItem(loggedIn, FontAwesome.USER, null);
            if (SecurityUtils.hasAnyRole(RoleEnum.ADMIN)) {
                dropDownParent.addItem("Users", FontAwesome.USERS, item -> Navigator.navigate(UsersView.NAME));
            }
            dropDownParent.addItem("Settings", FontAwesome.GEAR, selectedItem -> get().addWindow(settingsWindow));
            dropDownParent.addSeparator();
            dropDownParent.addItem("Logout", FontAwesome.SIGN_OUT, selectedItem -> EventBus.post(new LogoutEvent(this)));
            dropDownParent.setStyleName(Styles.MENUBAR_PUSH_RIGHT);
        }
        MenuBar.MenuItem titleItem = menuBar.addItem(appTitle + " | " + currentView.getNameCaption(), null);
        titleItem.setStyleName(Styles.MENUBAR_TITLE);
        titleItem.setEnabled(false);
        if (currentView != null)
            currentView.customizeMenuBar(menuBar).forEach(menuItem -> menuItem.setStyleName(Styles.MENUBAR_NAV_BUTTON));
    }
}