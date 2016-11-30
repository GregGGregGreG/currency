package org.baddev.currency.ui.component.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import lombok.RequiredArgsConstructor;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.core.security.utils.SecurityUtils;
import org.baddev.currency.ui.Styles;
import org.baddev.currency.ui.component.view.settings.AppearanceSettingsView;
import org.baddev.currency.ui.component.view.settings.NotificationSettingsView;
import org.baddev.currency.ui.component.view.settings.SecuritySettingsView;
import org.baddev.currency.ui.component.view.user.AccountDetailsView;
import org.baddev.currency.ui.component.view.user.admin.UsersView;
import org.baddev.currency.ui.core.component.view.AbstractView;
import org.baddev.currency.ui.core.component.window.form.FormWindow;
import org.baddev.currency.ui.core.util.EventBus;
import org.baddev.currency.ui.core.util.Navigator;
import org.baddev.currency.ui.event.LogoutEvent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * Created by IPotapchuk on 9/27/2016.
 */
@SpringViewDisplay
@RequiredArgsConstructor
public class MainView extends VerticalLayout implements ViewDisplay {

    private static final long serialVersionUID = -1222163377764852965L;

    private AbstractView currentView;
    private MenuBar menuBar;

    @Value("${app.title}") private String appTitle;

    private final ObjectProvider<NotificationSettingsView> notifSettingsViewProvider;
    private final ObjectProvider<AppearanceSettingsView>   appearanceSettingsViewProvider;
    private final ObjectProvider<SecuritySettingsView>     securitySettingsViewProvider;
    private final ObjectProvider<AccountDetailsView>       accountDetailsViewProvider;

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
        if (view instanceof AbstractView) currentView = (AbstractView) view;
        setupMenuBar();
        addComponent(currentView);
        setExpandRatio(currentView, 1.0f);
    }

    private void setupMenuBar() {
        menuBar.removeItems();
        String loggedIn = SecurityUtils.loggedInUserName();
        if (SecurityUtils.isLoggedIn() && !StringUtils.isEmpty(loggedIn.trim())) {
            MenuBar.MenuItem dropDownParent = menuBar.addItem(loggedIn, FontAwesome.USER, null);
            if (SecurityUtils.hasAnyRole(RoleEnum.ADMIN)) {
                dropDownParent.addItem("Users", FontAwesome.USERS, item -> Navigator.navigate(UsersView.NAME));
                dropDownParent.addSeparator();
            }
            dropDownParent.addItem("Account Details...", FontAwesome.EDIT, menuItem -> {
                FormWindow.show(accountDetailsViewProvider.getIfAvailable()
                        .withUserName(SecurityUtils.loggedInUserName()));
            });
            dropDownParent.addItem("Settings...", FontAwesome.GEAR, selectedItem -> {
                FormWindow.showTabs("Settings",
                        notifSettingsViewProvider.getIfAvailable(),
                        appearanceSettingsViewProvider.getIfAvailable(),
                        securitySettingsViewProvider.getIfAvailable());
            });
            dropDownParent.addSeparator();
            dropDownParent.addItem("Logout", FontAwesome.SIGN_OUT, selectedItem -> EventBus.post(
                    new LogoutEvent(UUID.randomUUID(), this, SecurityUtils.loggedInUserName())));
            dropDownParent.setStyleName(Styles.MENUBAR_PUSH_RIGHT);
        }
        MenuBar.MenuItem titleItem = menuBar.addItem(appTitle + " | " + currentView.getNameCaption(), null);
        titleItem.setStyleName(Styles.MENUBAR_TITLE);
        titleItem.setEnabled(false);
        if (currentView != null)
            currentView.customizeMenuBar(menuBar).forEach(menuItem -> menuItem.setStyleName(Styles.MENUBAR_NAV_BUTTON));
    }
}
