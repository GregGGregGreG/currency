package org.baddev.currency.ui.component.view.user.admin;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;
import lombok.NonNull;
import org.baddev.currency.core.api.UserService;
import org.baddev.currency.core.dto.RestrictionsDTO;
import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.event.binder.RestrictionsChangeEvent;
import org.baddev.currency.ui.event.binder.SafeProcessableBinderEvent;
import org.baddev.currency.ui.util.ButtonFactory;
import org.baddev.currency.ui.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.security.RolesAllowed;

import static org.baddev.currency.ui.util.FieldGroupUtils.fConf;
import static org.baddev.currency.ui.util.FormatUtils.bold;

/**
 * Created by IPotapchuk on 11/18/2016.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RolesAllowed({RoleEnum.ADMIN})
public class RestrictionsView extends AbstractFormView<RestrictionsDTO> {

    private String userName;
    private final UserService userService;

    public RestrictionsView(@NonNull UserService userService) {
        super(RestrictionsDTO.class, ValoTheme.PANEL_BORDERLESS);
        this.userService = userService;
    }

    @Required
    public RestrictionsView withUserName(@NonNull String userName) {
        this.userName = userName;
        setItem(userService.findOneUserByUserName(userName)
                .map(u -> new RestrictionsDTO(userName,
                        u.getAccNonLocked(),
                        u.getAccNonExpired(),
                        u.getCredNonExpired(),
                        u.getEnabled()))
                .orElseThrow(() -> new ServiceException("User " + userName + " not found")));
        return this;
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<RestrictionsDTO> binder, Button submitBtn) {
        map(fConf("accountEnabled", "Account Enabled", CheckBox.class),
                fConf("accountNotLocked", "Account Not Locked", CheckBox.class),
                fConf("accountNotExpired", "Account Not Expired", CheckBox.class),
                fConf("credentialsNotExpired", "Credentials Not Expired", CheckBox.class));
    }

    @Override
    public String getNameCaption() {
        return "Account Restrictions - " + bold(userName);
    }

    @Override
    protected Class<? extends SafeProcessableBinderEvent<RestrictionsDTO>> getEventType() {
        return RestrictionsChangeEvent.class;
    }

    @Override
    protected void commitSuccess(RestrictionsDTO formBean) {
        NotificationUtils.notifySuccess("User Restrictions Change", "Changes successfully saved");
    }

    @Override
    protected ButtonFactory.Mode getSubmitButtonMode() {
        return ButtonFactory.Mode.EDIT;
    }
}
