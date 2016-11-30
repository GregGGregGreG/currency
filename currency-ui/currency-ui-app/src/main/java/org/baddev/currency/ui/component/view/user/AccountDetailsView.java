package org.baddev.currency.ui.component.view.user;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;
import lombok.NonNull;
import org.baddev.currency.core.api.UserService;
import org.baddev.currency.core.dto.UserDetailsDTO;
import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.ui.core.component.view.AbstractFormView;
import org.baddev.currency.ui.core.event.SafeProcessableBinderEvent;
import org.baddev.currency.ui.core.util.ButtonFactory;
import org.baddev.currency.ui.core.util.FormatUtils;
import org.baddev.currency.ui.core.util.NotificationUtils;
import org.baddev.currency.ui.event.binder.AccountDetailsChangeEvent;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.security.RolesAllowed;

import static org.baddev.currency.ui.core.util.FieldGroupUtils.fConf;

/**
 * Created by IPotapchuk on 11/17/2016.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RolesAllowed({RoleEnum.ADMIN, RoleEnum.USER})
public class AccountDetailsView extends AbstractFormView<UserDetailsDTO> {

    private String userName;
    private final UserService userService;

    public AccountDetailsView(@NonNull UserService userService) {
        super(UserDetailsDTO.class, ValoTheme.PANEL_BORDERLESS);
        this.userService = userService;
    }

    @Required
    public AccountDetailsView withUserName(@NonNull String userName) {
        this.userName = userName;
        setItem(userService.findUserDetailsByUsername(userName)
                .map(iud -> iud.into(new UserDetailsDTO()))
                .orElseThrow(() -> new ServiceException("UserDetails for user [" + userName + "] not found")));
        return this;
    }

    @Override
    public String getNameCaption() {
        return "Account Details - " + FormatUtils.bold(userName);
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<UserDetailsDTO> binder, Button submitBtn) {
        map(fConf("firstName", "First Name", FontAwesome.INFO),
                fConf("lastName", "Last Name", FontAwesome.INFO),
                fConf("email", "Email", FontAwesome.AT));
    }

    @Override
    protected void commitSuccess(UserDetailsDTO formBean) {
        NotificationUtils.notifySuccess("Account Details Changes", "Changes successfully saved");
    }

    @Override
    protected Class<? extends SafeProcessableBinderEvent<UserDetailsDTO>> getEventType() {
        return AccountDetailsChangeEvent.class;
    }

    @Override
    protected ButtonFactory.Mode getSubmitButtonMode() {
        return ButtonFactory.Mode.EDIT;
    }
}
