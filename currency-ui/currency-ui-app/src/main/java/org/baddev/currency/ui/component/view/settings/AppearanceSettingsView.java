package org.baddev.currency.ui.component.view.settings;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.dto.AppearanceSettingsDTO;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.ui.Theme;
import org.baddev.currency.ui.core.component.view.AbstractFormView;
import org.baddev.currency.ui.core.event.SafeProcessableBinderEvent;
import org.baddev.currency.ui.core.util.NotificationUtils;
import org.baddev.currency.ui.core.util.VaadinSessionUtils;
import org.baddev.currency.ui.event.binder.AppearanceChangeEvent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by IPotapchuk on 11/14/2016.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RolesAllowed({RoleEnum.USER, RoleEnum.ADMIN})
public class AppearanceSettingsView extends AbstractFormView<AppearanceSettingsDTO> {

    private static final long serialVersionUID = -24757545714135288L;

    public AppearanceSettingsView() {
        super(AppearanceSettingsDTO.class,
                new AppearanceSettingsDTO(VaadinSessionUtils.getAttribute(UserPreferences.class).getThemeName()),
                ValoTheme.PANEL_BORDERLESS);
        setCloseOnCommitSuccessIfHasParentWindow(false);
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<AppearanceSettingsDTO> binder, Button submitBtn) {
        NativeSelect themeSelect = new NativeSelect("Theme",
                Collections.unmodifiableList(Arrays.asList(Theme.VALUES)));
        themeSelect.setItemCaption(Theme.DEFAULT, "Default");
        themeSelect.setItemCaption(Theme.FACEBOOK, "Facebook");
        themeSelect.setNewItemsAllowed(false);
        themeSelect.setNullSelectionAllowed(false);
        binder.bind(themeSelect, "themeName");
        formLayout.addComponents(themeSelect);
    }

    @Override
    protected Class<? extends SafeProcessableBinderEvent<AppearanceSettingsDTO>> getEventType() {
        return AppearanceChangeEvent.class;
    }

    @Override
    protected void commitSuccess(AppearanceSettingsDTO formBean) {
        NotificationUtils.notifySuccess("Appearance Settings Change", "Settings successfully changed");
    }

    @Override
    public String getNameCaption() {
        return "Appearance";
    }
}
