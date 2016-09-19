package org.baddev.currency.ui.component.view;

import com.google.common.eventbus.EventBus;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.baddev.currency.core.RoleEnum;
import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.jooq.schema.tables.pojos.User;
import org.baddev.currency.security.user.UserService;
import org.baddev.currency.ui.component.base.AbstractCcyGridView;
import org.baddev.currency.ui.component.window.SettingsWindow;
import org.baddev.currency.ui.component.window.UserDetailsWindow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.DeclareRoles;

/**
 * Created by IPotapchuk on 9/19/2016.
 */
@SpringView(name = UsersView.NAME)
@DeclareRoles({RoleEnum.ADMIN})
public class UsersView extends AbstractCcyGridView<IUser> {

    public static final String NAME = "admin";
    public static final String P_GEN_DETAILS_BTN = "gen_details";

    private final UserService userService;

    @Autowired
    public UsersView(SettingsWindow settingsWindow, EventBus bus, UserService userService) {
        super(settingsWindow, bus);
        this.userService = userService;
    }

    @Override
    protected void init() {
        setup(IUser.class, userService.findAll(), User.P_PASSWORD);
        grid.getColumn(User.P_ACC_NON_EXPIRED).setHeaderCaption("Not Expired");
        grid.getColumn(User.P_ENABLED).setRenderer(new ButtonRenderer(event -> {
            IUser user = (IUser) event.getItemId();
            user.setEnabled(!user.getEnabled());
            userService.update(user);
            refresh(userService.findAll(), User.P_ID, SortDirection.ASCENDING);
        })).setHeaderCaption("Not Locked");
        grid.getColumn(User.P_CRED_NON_EXPIRED).setHeaderCaption("Credentials Not Expired");

        addGeneratedButton(P_GEN_DETAILS_BTN, "Details", event -> {
            UserDetailsWindow window = beanFactory.getBean(UserDetailsWindow.class);
            window.show(() -> userService.findUserDetailsByUsername(((IUser) event.getItemId()).getUsername()));
        });

        grid.setColumnOrder(
                User.P_ID,
                User.P_USERNAME,
                User.P_ENABLED,
                User.P_ACC_NON_LOCKED,
                User.P_ACC_NON_EXPIRED,
                User.P_CRED_NON_EXPIRED,
                P_GEN_DETAILS_BTN);

        grid.sort(User.P_ID, SortDirection.ASCENDING);
    }

    @Override
    protected void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Rates", FontAwesome.DOLLAR, selectedItem -> navigateTo(RatesView.NAME));
        menuBar.addItem("Exchanges", FontAwesome.EXCHANGE, selectedItem -> navigateTo(ExchangesView.NAME));
        menuBar.addItem("Scheduler", FontAwesome.GEARS, selectedItem -> navigateTo(SchedulerView.NAME));
    }

    @Override
    protected void customizeTopBar(HorizontalLayout topBar) {

    }
}
