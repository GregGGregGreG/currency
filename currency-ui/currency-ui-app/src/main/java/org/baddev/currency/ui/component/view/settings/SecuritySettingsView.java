package org.baddev.currency.ui.component.view.settings;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;
import lombok.NonNull;
import org.baddev.currency.core.dto.SecuritySettingsDTO;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.ui.component.view.user.PasswordChangeView;
import org.baddev.currency.ui.core.component.view.AbstractFormView;
import org.baddev.currency.ui.core.component.window.form.FormWindow;
import org.baddev.currency.ui.core.event.SafeProcessableBinderEvent;
import org.baddev.currency.ui.core.util.NotificationUtils;
import org.baddev.currency.ui.core.util.VaadinSessionUtils;
import org.baddev.currency.ui.event.binder.SecurityChangeEvent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.security.RolesAllowed;

import static org.baddev.currency.ui.core.util.FieldGroupUtils.fConf;

/**
 * Created by IPotapchuk on 11/14/2016.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RolesAllowed({RoleEnum.USER, RoleEnum.ADMIN})
public class SecuritySettingsView extends AbstractFormView<SecuritySettingsDTO> implements Button.ClickListener {

    private ObjectProvider<PasswordChangeView> passChangeViewProvider;

    public SecuritySettingsView(@NonNull ObjectProvider<PasswordChangeView> passChangeViewProvider) {
        super(SecuritySettingsDTO.class,
                new SecuritySettingsDTO(
                        VaadinSessionUtils.getAttribute(UserPreferences.class).getTwoFactorAuth(),
                        VaadinSessionUtils.getAttribute(UserPreferences.class).getEmailSignIn()),
                ValoTheme.PANEL_BORDERLESS);
        this.passChangeViewProvider = passChangeViewProvider;
        setCloseOnCommitSuccessIfHasParentWindow(false);
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<SecuritySettingsDTO> binder, Button submitBtn) {
        Button pwdChangeBtn = new Button();
        pwdChangeBtn.setCaption("Change Password");
        pwdChangeBtn.setStyleName(ValoTheme.BUTTON_DANGER);
        pwdChangeBtn.addClickListener(event -> FormWindow.show(passChangeViewProvider.getIfAvailable()));
        formLayout.addComponents(pwdChangeBtn);
        map(fConf("twoFactorAuth", "Enable Two-Factor Authentication", CheckBox.class),
                fConf("signInWithEmail", "Enable Sign In With Email", CheckBox.class));
    }

    @Override
    protected Class<? extends SafeProcessableBinderEvent<SecuritySettingsDTO>> getEventType() {
        return SecurityChangeEvent.class;
    }

    @Override
    protected void commitSuccess(SecuritySettingsDTO formBean) {
        NotificationUtils.notifySuccess("Security Settings Change", "Changes successfully saved");
    }

    @Override
    public String getNameCaption() {
        return "Security";
    }

}
