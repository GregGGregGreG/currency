package org.baddev.currency.ui.component.view.user;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.dto.PasswordChangeDTO;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.core.security.utils.SecurityUtils;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.event.binder.PasswordChangeEvent;
import org.baddev.currency.ui.event.binder.SafeProcessableBinderEvent;
import org.baddev.currency.ui.util.NotificationUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.security.RolesAllowed;
import java.util.Objects;

import static org.baddev.currency.ui.util.FieldGroupUtils.fConf;
import static org.baddev.currency.ui.util.FormatUtils.bold;

/**
 * Created by IPotapchuk on 11/15/2016.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RolesAllowed({RoleEnum.ADMIN, RoleEnum.USER})
public class PasswordChangeView extends AbstractFormView<PasswordChangeDTO>{

    public PasswordChangeView() {
        super(PasswordChangeDTO.class, ValoTheme.PANEL_BORDERLESS);
        setFormChangedCheckEnabled(false);
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<PasswordChangeDTO> binder, Button submitBtn) {
        map(fConf("currentPassword", "Enter Current Password", FontAwesome.LOCK, PasswordField.class),
                fConf("password", "Enter New Password", FontAwesome.LOCK, PasswordField.class),
                fConf("passwordConfirm", "Confirm New Password", FontAwesome.LOCK, PasswordField.class,
                        value -> {
                            if (!Objects.equals(value, binder.getField("password").getValue())) {
                                throw new Validator.InvalidValueException("passwords must match");
                            }
                        }));
    }

    @Override
    protected Class<? extends SafeProcessableBinderEvent<PasswordChangeDTO>> getEventType() {
        return PasswordChangeEvent.class;
    }

    @Override
    protected void commitSuccess(PasswordChangeDTO formBean) {
        NotificationUtils.notifySuccess("Password Change", "Password successfully changed");
    }

    @Override
    protected void commitFailed(FieldGroup binder, Exception e) {
        binder.getField("currentPassword").clear();
    }

    @Override
    public String getNameCaption() {
        return "Password Change - "+ bold(SecurityUtils.loggedInUserName());
    }
}
