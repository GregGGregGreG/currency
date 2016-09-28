package org.baddev.currency.ui.component.window;

import com.vaadin.data.Property;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.listener.NotificationListener;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.mail.ExchangeCompletionMailer;
import org.baddev.currency.security.utils.SecurityUtils;
import org.baddev.currency.ui.CurrencyUI;
import org.baddev.currency.ui.util.AppSettingsUtils;
import org.baddev.currency.ui.util.Theme;
import org.baddev.currency.ui.util.VaadinSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by IPotapchuk on 6/22/2016.
 */
@SpringComponent
@UIScope
public class SettingsWindow extends Window {

    private final TabSheet tabSheet = new TabSheet();

    @Autowired
    private ExchangeCompletionMailer mailListener;

    @PostConstruct
    private void init() {
        setCaption("Settings");
        setWidth(600.0f, Unit.PIXELS);
        setModal(true);
        setResizable(false);

        configureTabsheet();

        CheckBox mailNotifCb = new CheckBox("Mail on exchange task completion");
        mailNotifCb.addValueChangeListener(e -> valChange(e, mailListener));
        CheckBox uiNotifCb = new CheckBox("UI notification on exchange task completion");
        uiNotifCb.addValueChangeListener(e -> valChange(e, (CurrencyUI) getUI()));

        tabSheet.addTab(newTab(uiNotifCb, mailNotifCb), "Notifications");

        NativeSelect themeSelect = new NativeSelect("Theme",
                Collections.unmodifiableList(Arrays.asList(Theme.VALUES)));
        themeSelect.setItemCaption(Theme.DEFAULT, "Default");
        themeSelect.setItemCaption(Theme.FACEBOOK, "Facebook");
        themeSelect.setNewItemsAllowed(false);
        themeSelect.setNullSelectionAllowed(false);
        themeSelect.addValueChangeListener(e -> {
            String newTheme = String.valueOf(e.getProperty().getValue());
            if (!newTheme.equals(CurrencyUI.getCurrent().getTheme())) {
                VaadinSessionUtils.getSessionAttribute(UserPreferences.class).setThemeName(newTheme);
                AppSettingsUtils.applyUISharedUserTheme();
            }
        });

        addAttachListener(e -> {
            uiNotifCb.setValue(VaadinSessionUtils.getSessionAttribute(UserPreferences.class).getUiNotifications());
            mailNotifCb.setValue(VaadinSessionUtils.getSessionAttribute(UserPreferences.class).getMailNotifications());
            themeSelect.setValue(VaadinSessionUtils.getSessionAttribute(UserPreferences.class).getThemeName());
        });

        tabSheet.addTab(newTab(themeSelect), "Appearance");

        setContent(tabSheet);
        center();
    }

    private void configureTabsheet() {
        tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
    }

    private static FormLayout newTab(Component... components) {
        FormLayout fl = new FormLayout(components);
        fl.setMargin(true);
        fl.setSizeFull();
        return fl;
    }

    private void valChange(Property.ValueChangeEvent e, NotificationListener l) {
        boolean checked = (boolean) e.getProperty().getValue();
        if (l instanceof CurrencyUI)
            VaadinSessionUtils.getSessionAttribute(UserPreferences.class).setUiNotifications(checked);
        else if (l instanceof ExchangeCompletionMailer) {
            ((ExchangeCompletionMailer) l).setEmail(SecurityUtils.getUserDetails().getEmail());
            VaadinSessionUtils.getSessionAttribute(UserPreferences.class).setMailNotifications(true);
        }
    }
}
