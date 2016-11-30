package org.baddev.currency.ui.component.view.user.admin;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.api.UserService;
import org.baddev.currency.core.dto.UserPasswordChangeDTO;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.jooq.schema.tables.daos.RoleDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IRole;
import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.ui.Styles;
import org.baddev.currency.ui.component.view.feature.ExcelExportView;
import org.baddev.currency.ui.component.view.feature.ExchangesView;
import org.baddev.currency.ui.component.view.feature.RatesView;
import org.baddev.currency.ui.component.view.feature.SchedulerView;
import org.baddev.currency.ui.component.view.user.AccountDetailsView;
import org.baddev.currency.ui.core.component.toolbar.GridButtonToolbar;
import org.baddev.currency.ui.core.component.view.AbstractGridView;
import org.baddev.currency.ui.core.component.window.form.FormWindow;
import org.baddev.currency.ui.core.model.grid.FilterConfig;
import org.baddev.currency.ui.core.model.grid.FilterKind;
import org.baddev.currency.ui.core.util.ButtonFactory;
import org.baddev.currency.ui.core.util.Navigator;
import org.baddev.currency.ui.core.util.NotificationUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.security.RolesAllowed;
import java.util.*;
import java.util.stream.Collectors;

import static org.baddev.currency.jooq.schema.tables.pojos.User.*;
import static org.baddev.currency.ui.core.util.FormatUtils.bold;
import static org.baddev.currency.ui.core.util.FormatUtils.boldInQuotes;

/**
 * Created by IPotapchuk on 9/19/2016.
 */
@SpringView(name = UsersView.NAME)
@RolesAllowed({RoleEnum.ADMIN})
public final class UsersView extends AbstractGridView<IUser> {

    private static final long serialVersionUID = 6755374220474749056L;

    public static final String NAME = "users";

    private final RoleDao                            roleDao;
    private final UserService                        userService;
    private final ObjectProvider<AccountDetailsView> accountDetailsViewProvider;
    private final ObjectProvider<RestrictionsView>   restrictionsViewProvider;
    private final ObjectProvider<ExcelExportView>    exportViewProvider;

    public UsersView(RoleDao roleDao,
                     UserService userService,
                     ObjectProvider<AccountDetailsView> accountDetailsViewProvider,
                     ObjectProvider<RestrictionsView> restrictionsViewProvider, ObjectProvider<ExcelExportView> exportViewProvider) {
        super(IUser.class, P_PASSWORD, P_ID);
        this.roleDao = roleDao;
        this.userService = userService;
        this.accountDetailsViewProvider = accountDetailsViewProvider;
        this.restrictionsViewProvider = restrictionsViewProvider;
        this.exportViewProvider = exportViewProvider;
    }

    @Override
    protected final void setup(Grid grid) {
        List<Grid.Column> iconRenderedColumns = new ArrayList<>();
        iconRenderedColumns.add(grid.getColumn(P_ACC_NON_LOCKED).setHeaderCaption("Account Lock Status"));
        iconRenderedColumns.add(grid.getColumn(P_ACC_NON_EXPIRED).setHeaderCaption("Account Expiration Status"));
        iconRenderedColumns.add(grid.getColumn(P_ENABLED).setHeaderCaption("Account Enabled"));
        iconRenderedColumns.add(grid.getColumn(P_CRED_NON_EXPIRED).setHeaderCaption("Password Expiration Status"));

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

        grid.setCellStyleGenerator(cell -> {
            Object pid = cell.getPropertyId();
            if ((P_ENABLED.equals(pid) || P_ACC_NON_LOCKED.equals(pid)
                    || P_ACC_NON_EXPIRED.equals(pid) || P_CRED_NON_EXPIRED.equals(pid))
                    && Boolean.FALSE.equals(cell.getValue())) {
                return Styles.GRID_CELL_WARN;
            } else return null;
        });

        grid.setCellDescriptionGenerator(cell -> {
            if (P_ENABLED.equals(cell.getPropertyId())) {
                return ((Boolean) cell.getValue()) ? "Enabled" : "Disabled";
            } else if (P_ACC_NON_EXPIRED.equals(cell.getPropertyId()) || P_CRED_NON_EXPIRED.equals(cell.getPropertyId())) {
                return ((Boolean) cell.getValue()) ? "Not Expired" : "Expired";
            } else if (P_ACC_NON_LOCKED.equals(cell.getPropertyId())) {
                return ((Boolean) cell.getValue()) ? "Not Locked" : "Locked";
            }
            return "";
        });

        grid.setColumnOrder(
                P_USERNAME,
                P_ENABLED,
                P_ACC_NON_LOCKED,
                P_ACC_NON_EXPIRED,
                P_CRED_NON_EXPIRED);

        setSortOrder(P_ID, SortDirection.ASCENDING);
        setHidableColumns(true);
    }

    @Override
    protected Collection<? extends IUser> getItems() {
        return userService.findAll();
    }

    @Override
    protected void postRefresh(Collection<? extends IUser> data) {
        addRowFilter(new FilterConfig()
                .setPropId(P_USERNAME)
                .setKind(FilterKind.TEXT)
                .setTextAutocomplete(true)
                .setSelectOptions(data.stream().map(IUser::getUsername).collect(Collectors.toList())));
        addRowFilter(new FilterConfig()
                .setPropId(P_ENABLED)
                .setKind(FilterKind.SELECT)
                .setSelectOptions(data.stream().map(user -> user.getEnabled().toString()).collect(Collectors.toList())));
        addRowFilter(new FilterConfig()
                .setPropId(P_ACC_NON_LOCKED)
                .setKind(FilterKind.SELECT)
                .setSelectOptions(data.stream().map(user -> user.getAccNonLocked().toString()).collect(Collectors.toList())));
        addRowFilter(new FilterConfig()
                .setPropId(P_ACC_NON_EXPIRED)
                .setKind(FilterKind.SELECT)
                .setSelectOptions(data.stream().map(user -> user.getAccNonExpired().toString()).collect(Collectors.toList())));
        addRowFilter(new FilterConfig()
                .setPropId(P_CRED_NON_EXPIRED)
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
    protected void customizeGridBar(HorizontalLayout gridBar) {
        final GridButtonToolbar toolbar = new GridButtonToolbar(getGrid(), true)
                .withExportBtn(exportViewProvider.getObject(this), ValoTheme.BUTTON_FRIENDLY)
                .withActionBtn("Details...", FontAwesome.INFO_CIRCLE, selectedUsers -> {
                    String uname = ((Set<IUser>) selectedUsers).iterator().next().getUsername();
                    FormWindow.show(accountDetailsViewProvider.getIfAvailable().withUserName(uname));
                })
                .withActionBtn("Roles...", FontAwesome.CHECK_SQUARE_O, selectedUsers -> {
                    IUser user = ((Set<IUser>) selectedUsers).iterator().next();
                    FormWindow.show(new FormWindow.Config<IRole>(FormWindow.Mode.EDIT)
                            .setCaption("User Roles - " + bold(user.getUsername()))
                            .setLhs(roleDao.findAll())
                            .setRhs(userService.findUserRoles(user.getId()))
                            .setItemCaptionProducer(IRole::getRoleName)
                            .setOnCommitSuccess((Set<IRole> roleSet) -> {
                                userService.updateUserRoles(user.getId(), roleSet.stream().map(IRole::getId).collect(Collectors.toList()));
                            }));
                })
                .withActionBtn("Restrictions...", FontAwesome.BAN, selectedUsers -> {
                    IUser user = ((Set<IUser>) selectedUsers).iterator().next();
                    RestrictionsView view = restrictionsViewProvider.getIfAvailable().withUserName(user.getUsername());
                    view.addFormCommittedListener(formBean -> {
                        refresh(userService.findAll(), P_ID, SortDirection.ASCENDING);
                    });
                    FormWindow.show(view);
                })
                .withActionBtn("Change Password...", FontAwesome.LOCK, selectedUsers -> {
                    IUser user = ((Set<IUser>) selectedUsers).iterator().next();
                    FormWindow.show(new FormWindow.Config<UserPasswordChangeDTO>(FormWindow.Mode.EDIT)
                            .setBeanClass(UserPasswordChangeDTO.class)
                            .setFormBean(new UserPasswordChangeDTO(user.getId(), ""))
                            .addFieldConfig("newPassword", "New Password", PasswordField.class)
                            .setOnCommitSuccess((Set<UserPasswordChangeDTO> set) -> userService.changeUserPassword(set.iterator().next()))
                            .setCaption("Password Change - " + bold(user.getUsername())));
                })
                .withActionFactoryBtn(ButtonFactory.Mode.REMOVE, selectedUsers -> {
                    String uname = ((Set<IUser>) selectedUsers).iterator().next().getUsername();
                    ConfirmDialog.show(UI.getCurrent(),
                            "Removal Confirmation",
                            "Are you really sure you want to remove user " + boldInQuotes(uname) + "?",
                            "Yes",
                            "Cancel",
                            dialog -> {
                                if (dialog.isConfirmed()) {
                                    userService.delete(uname);
                                    refresh(userService.findAll(), P_ID, SortDirection.ASCENDING);
                                    NotificationUtils.notifySuccess("User Removal",
                                            "User " + boldInQuotes(uname) + " successfully removed");
                                }
                            }).setContentMode(ConfirmDialog.ContentMode.HTML);
                });
        gridBar.addComponent(toolbar);
        gridBar.setComponentAlignment(toolbar, Alignment.MIDDLE_RIGHT);
    }

    @Override
    public String getNameCaption() {
        return "Users";
    }
}
