package org.baddev.currency.ui.component.view;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.baddev.currency.core.RoleEnum;
import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.baddev.currency.jooq.schema.tables.pojos.User;
import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;
import org.baddev.currency.security.dto.UserPasswordChangeDTO;
import org.baddev.currency.security.user.UserService;
import org.baddev.currency.ui.component.GridButtonToolbar;
import org.baddev.currency.ui.component.base.AbstractCcyGridView;
import org.baddev.currency.ui.component.window.FormWindow;
import org.baddev.currency.ui.component.window.SettingsWindow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.DeclareRoles;
import java.util.Set;

import static org.baddev.currency.ui.util.FormatUtils.bold;

/**
 * Created by IPotapchuk on 9/19/2016.
 */
@SpringView(name = UsersView.NAME)
@DeclareRoles({RoleEnum.ADMIN})
public final class UsersView extends AbstractCcyGridView<IUser> {

    public static final String NAME = "admin";

    private final UserService userService;
    private final GridButtonToolbar toolbar = new GridButtonToolbar(grid);

    @Autowired
    public UsersView(SettingsWindow settingsWindow, EventBus bus, UserService userService) {
        super(settingsWindow, bus);
        this.userService = userService;
    }

    @Override
    protected void postInit(VerticalLayout rootLayout) {
        setup(IUser.class, userService.findAll(), User.P_PASSWORD);

        grid.getColumn(User.P_ACC_NON_LOCKED).setHeaderCaption("Not Locked");
        grid.getColumn(User.P_ACC_NON_EXPIRED).setHeaderCaption("Not Expired");
        grid.getColumn(User.P_ENABLED).setHeaderCaption("Enabled");
        grid.getColumn(User.P_CRED_NON_EXPIRED).setHeaderCaption("Credentials Not Expired");

        grid.setColumnOrder(
                User.P_ID,
                User.P_USERNAME,
                User.P_ENABLED,
                User.P_ACC_NON_LOCKED,
                User.P_ACC_NON_EXPIRED,
                User.P_CRED_NON_EXPIRED);

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
        toolbar.createButton("Edit Details", o -> {
                    String uname = ((Set<IUser>) o).iterator().next().getUsername();
                    new FormWindow<>(IUserDetails.class, userService.findUserDetailsByUsername(uname),
                            ImmutableMap.of("First Name", UserDetails.P_FIRST_NAME,
                                    "Last Name", UserDetails.P_LAST_NAME,
                                    "Email", UserDetails.P_EMAIL))
                            .withSubmitActionProvider(b -> userService.update(null, b.getItemDataSource().getBean()))
                            .showEdit("User Details - <b>" + uname + "</b>");
                })
                .createButton("Remove", o -> userService.delete(((Set<IUser>) o).iterator().next().getUsername()))
                .createButton("Change Password", o -> {
                    IUser user = ((Set<IUser>) o).iterator().next();
                    new FormWindow<>(UserPasswordChangeDTO.class,
                            new UserPasswordChangeDTO(user.getId(), ""),
                            ImmutableMap.of("New Password", "newPassword"),
                            ImmutableMap.of("newPassword", PasswordField.class))
                            .withSubmitActionProvider(p -> userService.changeUserPassword(p.getItemDataSource().getBean()))
                            .showEdit("Password Change - " + bold(user.getUsername()));
                });

        topBar.addComponent(toolbar);
        topBar.setComponentAlignment(toolbar, Alignment.MIDDLE_RIGHT);
    }
}
