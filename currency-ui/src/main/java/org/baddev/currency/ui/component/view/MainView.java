package org.baddev.currency.ui.component.view;

import com.google.common.collect.ImmutableMap;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import org.baddev.currency.core.api.UserService;
import org.baddev.currency.core.dto.UserDetailsDTO;
import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.core.security.utils.SecurityUtils;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;
import org.baddev.currency.ui.component.view.base.AbstractView;
import org.baddev.currency.ui.component.window.SettingsWindow;
import org.baddev.currency.ui.component.window.form.FormWindow;
import org.baddev.currency.ui.security.event.LogoutEvent;
import org.baddev.currency.ui.util.EventBus;
import org.baddev.currency.ui.util.FormatUtils;
import org.baddev.currency.ui.util.Navigator;
import org.baddev.currency.ui.util.Styles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Set;

import static org.baddev.currency.ui.CurrencyUI.get;

/**
 * Created by IPotapchuk on 9/27/2016.
 */
@SpringComponent
@UIScope
public class MainView extends VerticalLayout implements ViewDisplay {

    private static final long serialVersionUID = -1222163377764852965L;

    private AbstractView currentView;
    private MenuBar menuBar;
    @Autowired
    private SettingsWindow settingsWindow;
    @Value("${app.title}")
    private String appTitle;
    @Autowired
    private UserService userService;

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
            dropDownParent.addItem("Account Details", FontAwesome.EDIT, menuItem -> {
                FormWindow.show(new FormWindow.Config<UserDetailsDTO>(FormWindow.Mode.EDIT)
                        .setBeanClass(UserDetailsDTO.class)
                        .setFormBean(userService.findUserDetailsByUsername(loggedIn)
                                .map(ud -> ud.into(new UserDetailsDTO()))
                                .orElseThrow(() -> new ServiceException("User Details not found for user " + loggedIn)))
                        .setCaption("Account Details - "+ FormatUtils.bold(loggedIn))
                        .setOnCommitSuccess((Set<UserDetailsDTO> detailsSet) -> {
                            if (detailsSet.iterator().hasNext()) {
                                IUserDetails details = detailsSet.iterator().next();
                                userService.update(null, details);
                                SecurityUtils.setUserDetails(details);
                            }
                        }).setCaptionToPropertyIdMap(ImmutableMap.of(
                                "First Name", UserDetails.P_FIRST_NAME,
                                "Last Name", UserDetails.P_LAST_NAME,
                                "Email", UserDetails.P_EMAIL))
                );
            });
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
