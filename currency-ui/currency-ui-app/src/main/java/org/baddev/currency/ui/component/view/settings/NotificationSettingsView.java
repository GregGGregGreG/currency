package org.baddev.currency.ui.component.view.settings;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.dto.NotificationSettingsDTO;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.ui.core.component.view.AbstractFormView;
import org.baddev.currency.ui.core.event.SafeProcessableBinderEvent;
import org.baddev.currency.ui.core.util.NotificationUtils;
import org.baddev.currency.ui.core.util.VaadinSessionUtils;
import org.baddev.currency.ui.event.binder.NotificationChangeEvent;
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
public class NotificationSettingsView extends AbstractFormView<NotificationSettingsDTO> {

    private static final long serialVersionUID = -3257441220043658251L;

    public NotificationSettingsView() {
        super(NotificationSettingsDTO.class,
                new NotificationSettingsDTO(
                        VaadinSessionUtils.getAttribute(UserPreferences.class).getMailNotifications(),
                        VaadinSessionUtils.getAttribute(UserPreferences.class).getUiNotifications()),
                ValoTheme.PANEL_BORDERLESS);
        setCloseOnCommitSuccessIfHasParentWindow(false);
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<NotificationSettingsDTO> binder, Button submitBtn) {
        map(fConf("mailOnExchangeTaskCompletion", "Mail on exchange task completion", CheckBox.class),
                fConf("uiNotifOnExchangeTaskCompletion", "UI notification on exchange task completion", CheckBox.class));
    }

    @Override
    protected Class<? extends SafeProcessableBinderEvent<NotificationSettingsDTO>> getEventType() {
        return NotificationChangeEvent.class;
    }

    @Override
    protected void commitSuccess(NotificationSettingsDTO formBean) {
        NotificationUtils.notifySuccess("Notification Settings Change", "Settings successfully changed");
    }

    @Override
    public String getNameCaption() {
        return "Notifications";
    }
}
