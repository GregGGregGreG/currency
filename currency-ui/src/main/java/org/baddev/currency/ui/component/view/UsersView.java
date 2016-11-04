package org.baddev.currency.ui.component.view;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.api.UserService;
import org.baddev.currency.core.dto.UserDetailsDTO;
import org.baddev.currency.core.dto.UserPasswordChangeDTO;
import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.core.util.RoleEnum;
import org.baddev.currency.jooq.schema.tables.daos.RoleDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IRole;
import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.baddev.currency.jooq.schema.tables.pojos.User;
import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;
import org.baddev.currency.security.utils.SecurityUtils;
import org.baddev.currency.ui.component.base.VerticalSpacedLayout;
import org.baddev.currency.ui.component.toolbar.GridButtonToolbar;
import org.baddev.currency.ui.component.view.base.AbstractCcyGridView;
import org.baddev.currency.ui.component.window.form.FormWindow;
import org.baddev.currency.ui.util.Navigator;
import org.baddev.currency.ui.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.security.DeclareRoles;
import java.util.*;
import java.util.stream.Collectors;

import static org.baddev.currency.ui.util.FormatUtils.bold;
import static org.baddev.currency.ui.util.FormatUtils.boldInQuotes;

/**
 * Created by IPotapchuk on 9/19/2016.
 */
@SpringView(name = UsersView.NAME)
@DeclareRoles({RoleEnum.ADMIN})
public final class UsersView extends AbstractCcyGridView<IUser> {

    private static final long serialVersionUID = 6755374220474749056L;
    public static final String NAME = "users";

    @Autowired
    private RoleDao roleDao;
    private final UserService userService;
    private final GridButtonToolbar toolbar = new GridButtonToolbar(grid);

    @Autowired
    public UsersView(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void postInit(VerticalSpacedLayout rootLayout) {
        setup(IUser.class, userService.findAll(), User.P_PASSWORD);

        List<Grid.Column> iconRenderedColumns = new ArrayList<>();
        iconRenderedColumns.add(grid.getColumn(User.P_ACC_NON_LOCKED).setHeaderCaption("Account Lock Status"));
        iconRenderedColumns.add(grid.getColumn(User.P_ACC_NON_EXPIRED).setHeaderCaption("Account Expiration Status"));
        iconRenderedColumns.add(grid.getColumn(User.P_ENABLED).setHeaderCaption("Account Enabled"));
        iconRenderedColumns.add(grid.getColumn(User.P_CRED_NON_EXPIRED).setHeaderCaption("Password Expiration Status"));

        iconRenderedColumns.forEach(c -> c.setRenderer(
                new HtmlRenderer(),
                new Converter<String, Boolean>() {
                    @Override
                    public Boolean convertToModel(String value, Class<? extends Boolean> targetType, Locale locale) throws ConversionException {
                        return Boolean.valueOf(value);
                    }

                    @Override
                    public String convertToPresentation(Boolean value, Class<? extends String> targetType, Locale locale) throws ConversionException {
                        return value ? FontAwesome.CHECK_CIRCLE.getHtml() : FontAwesome.EXCLAMATION_CIRCLE.getHtml();
                    }

                    @Override
                    public Class<Boolean> getModelType() {
                        return Boolean.class;
                    }

                    @Override
                    public Class<String> getPresentationType() {
                        return String.class;
                    }
                }
        ));

        grid.setCellDescriptionGenerator(cell -> {
            if (User.P_ENABLED.equals(cell.getPropertyId())) {
                return ((Boolean) cell.getValue()) ? "Enabled" : "Disabled";
            } else if (User.P_ACC_NON_EXPIRED.equals(cell.getPropertyId()) || User.P_CRED_NON_EXPIRED.equals(cell.getPropertyId())) {
                return ((Boolean) cell.getValue()) ? "Not Expired" : "Expired";
            } else if (User.P_ACC_NON_LOCKED.equals(cell.getPropertyId())) {
                return ((Boolean) cell.getValue()) ? "Not Locked" : "Locked";
            }
            return "";
        });

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
    protected void postRefresh(Collection<? extends IUser> data) {
        addRowFilter(new FilterConfig()
                .setPropId(User.P_USERNAME)
                .setKind(FilterKind.TEXT));
        addRowFilter(new FilterConfig()
                .setPropId(User.P_ENABLED)
                .setKind(FilterKind.SELECT)
                .setSelectOptions(data.stream().map(user -> user.getEnabled().toString()).collect(Collectors.toList())));
        addRowFilter(new FilterConfig()
                .setPropId(User.P_ACC_NON_LOCKED)
                .setKind(FilterKind.SELECT)
                .setSelectOptions(data.stream().map(user -> user.getAccNonLocked().toString()).collect(Collectors.toList())));
        addRowFilter(new FilterConfig()
                .setPropId(User.P_ACC_NON_EXPIRED)
                .setKind(FilterKind.SELECT)
                .setSelectOptions(data.stream().map(user -> user.getAccNonExpired().toString()).collect(Collectors.toList())));
        addRowFilter(new FilterConfig()
                .setPropId(User.P_CRED_NON_EXPIRED)
                .setKind(FilterKind.SELECT)
                .setSelectOptions(data.stream().map(user -> user.getCredNonExpired().toString()).collect(Collectors.toList())));
    }

    @Override
    public Collection<MenuBar.MenuItem> customizeMenuBar(MenuBar menuBar) {
        return Arrays.asList(
                menuBar.addItem("Rates", FontAwesome.DOLLAR, selectedItem -> Navigator.navigate(RatesView.NAME)),
                menuBar.addItem("Exchanges", FontAwesome.EXCHANGE, selectedItem -> Navigator.navigate(ExchangesView.NAME)),
                menuBar.addItem("Scheduler", FontAwesome.GEARS, selectedItem -> Navigator.navigate(SchedulerView.NAME)));
    }

    @Override
    protected void customizeGridBar(HorizontalLayout topBar) {
        toolbar.withButton("Details", selectedUsers -> {
            String uname = ((Set<IUser>) selectedUsers).iterator().next().getUsername();
            FormWindow.show(new FormWindow.Config<UserDetailsDTO>(FormWindow.Mode.EDIT)
                    .setBeanClass(UserDetailsDTO.class)
                    .setFormBean(userService.findUserDetailsByUsername(uname)
                            .map(ud -> ud.into(new UserDetailsDTO()))
                            .orElseThrow(() -> new ServiceException("User Details not found for user " + uname)))
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
                    .setCaption("User Details - " + bold(uname))
            );
        }).withButton("Roles", selectedUsers -> {
            IUser user = ((Set<IUser>) selectedUsers).iterator().next();
            FormWindow.show(new FormWindow.Config<IRole>(FormWindow.Mode.EDIT)
                    .setCaption("User Roles - " + bold(user.getUsername()))
                    .setWidth(500f)
                    .setLhs(roleDao.findAll())
                    .setRhs(userService.findUserRoles(user.getId()))
                    .setItemCaptionProducer(IRole::getRoleName)
                    .setOnCommitSuccess((Set<IRole> roleSet) -> {
                        userService.updateUserRoles(user.getId(), roleSet.stream().map(IRole::getId).collect(Collectors.toList()));
                    }));
        }).withButton("Restrictions", selectedUsers -> {
            IUser user = ((Set<IUser>) selectedUsers).iterator().next();
            FormWindow.show(new FormWindow.Config<IUser>(FormWindow.Mode.EDIT)
                    .setBeanClass(IUser.class)
                    .setFormBean(user)
                    .setCaptionToPropertyIdMap(ImmutableMap.of(
                            "Account Enabled", User.P_ENABLED,
                            "Account Not Expired", User.P_ACC_NON_EXPIRED,
                            "Account Not Locked", User.P_ACC_NON_LOCKED,
                            "Credentials Not Expired", User.P_CRED_NON_EXPIRED))
                    .setPropertyIdToFieldTypeMap(ImmutableMap.of(
                            User.P_ENABLED, CheckBox.class,
                            User.P_ACC_NON_EXPIRED, CheckBox.class,
                            User.P_ACC_NON_LOCKED, CheckBox.class,
                            User.P_CRED_NON_EXPIRED, CheckBox.class))
                    .setCaption("Account Restrictions - " + bold(user.getUsername()))
                    .setWidth(400f)
                    .setOnCommitSuccess((Set<IUser> userSet) -> {
                        userService.update(userSet.iterator().next(), null);
                        refresh(userService.findAll(), User.P_ID, SortDirection.ASCENDING);
                    }));
        }).withButton("Change Password", selectedUsers -> {
            IUser user = ((Set<IUser>) selectedUsers).iterator().next();
            FormWindow.show(new FormWindow.Config<UserPasswordChangeDTO>(FormWindow.Mode.EDIT)
                    .setBeanClass(UserPasswordChangeDTO.class)
                    .setFormBean(new UserPasswordChangeDTO(user.getId(), ""))
                    .setCaptionToPropertyIdMap(ImmutableMap.of("New Password", "newPassword"))
                    .setPropertyIdToFieldTypeMap(ImmutableMap.of("newPassword", PasswordField.class))
                    .setOnCommitSuccess((Set<UserPasswordChangeDTO> set) -> userService.changeUserPassword(set.iterator().next()))
                    .setCaption("Password Change - " + bold(user.getUsername())));
        }).withButtonStyled("Remove", selectedUsers -> {
            String uname = ((Set<IUser>) selectedUsers).iterator().next().getUsername();
            ConfirmDialog.show(UI.getCurrent(),
                    "Removal Confirmation",
                    "Are you really sure you want to remove user " + boldInQuotes(uname) + "?",
                    "Yes",
                    "Cancel",
                    dialog -> {
                        if (dialog.isConfirmed()) {
                            userService.delete(uname);
                            refresh(userService.findAll(), User.P_ID, SortDirection.ASCENDING);
                            NotificationUtils.notifySuccess("User Removal",
                                    "User " + boldInQuotes(uname) + " successfully removed");
                        }
                    }).setContentMode(ConfirmDialog.ContentMode.HTML);
        }, ValoTheme.BUTTON_DANGER);
        topBar.addComponent(toolbar);
        topBar.setComponentAlignment(toolbar, Alignment.MIDDLE_RIGHT);
    }

    @Override
    public String getNameCaption() {
        return "Users";
    }
}
