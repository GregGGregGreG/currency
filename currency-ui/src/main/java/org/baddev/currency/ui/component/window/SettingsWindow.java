package org.baddev.currency.ui.component.window;

import com.google.common.collect.ImmutableMap;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.common.event.EventPublisher;
import org.baddev.currency.core.api.UserService;
import org.baddev.currency.core.dto.PasswordChangeDTO;
import org.baddev.currency.core.security.utils.SecurityUtils;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.mail.listener.MailExchangeCompletionListener;
import org.baddev.currency.ui.CurrencyUI;
import org.baddev.currency.ui.component.window.form.FormWindow;
import org.baddev.currency.ui.listener.UIExchangeCompletionListener;
import org.baddev.currency.ui.util.FormatUtils;
import org.baddev.currency.ui.util.Theme;
import org.baddev.currency.ui.util.VaadinSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Created by IPotapchuk on 6/22/2016.
 */
@SpringComponent
@UIScope
public class SettingsWindow extends Window {

    private static final long serialVersionUID = 4376763543379699312L;

    private final TabSheet tabSheet = new TabSheet();

    @Autowired private EventPublisher                 eventPublisher;
    @Autowired private MailExchangeCompletionListener mailListener;
    @Autowired private UIExchangeCompletionListener   uiListener;
    @Autowired private UserService userService;

    @PostConstruct
    private void init() {
        setCaption("Settings");
        setWidth(600.0f, Unit.PIXELS);
        setModal(true);
        setResizable(false);

        configureTabsheet();

        CheckBox mailNotifCb = new CheckBox("Mail on exchange task completion");
        mailNotifCb.addValueChangeListener(event -> {
            if (Boolean.TRUE.equals(event.getProperty().getValue())) {
                eventPublisher.subscribe(mailListener);
            } else eventPublisher.unsubscribe(mailListener);
            VaadinSessionUtils.getAttribute(UserPreferences.class).setMailNotifications((Boolean) event.getProperty().getValue());
        });

        CheckBox uiNotifCb = new CheckBox("UI notification on exchange task completion");
        uiNotifCb.addValueChangeListener(event -> {
            if (Boolean.TRUE.equals(event.getProperty().getValue())) {
                eventPublisher.subscribe(uiListener);
            } else eventPublisher.unsubscribe(uiListener);
            VaadinSessionUtils.getAttribute(UserPreferences.class).setUiNotifications((Boolean) event.getProperty().getValue());
        });

        addTab("Notifications", uiNotifCb, mailNotifCb);

        NativeSelect themeSelect = new NativeSelect("Theme",
                Collections.unmodifiableList(Arrays.asList(Theme.VALUES)));
        themeSelect.setItemCaption(Theme.DEFAULT, "Default");
        themeSelect.setItemCaption(Theme.FACEBOOK, "Facebook");
        themeSelect.setNewItemsAllowed(false);
        themeSelect.setNullSelectionAllowed(false);
        themeSelect.addValueChangeListener(e -> {
            String newTheme = String.valueOf(e.getProperty().getValue());
            if (!newTheme.equals(CurrencyUI.getCurrent().getTheme())) {
                VaadinSessionUtils.getAttribute(UserPreferences.class).setThemeName(newTheme);
                VaadinSessionUtils.getSession().getUIs().forEach(ui -> {
                    ui.setTheme(VaadinSessionUtils.getAttribute(UserPreferences.class).getThemeName());
                });
            }
        });

        addAttachListener(e -> {
            uiNotifCb.setValue(VaadinSessionUtils.getAttribute(UserPreferences.class).getUiNotifications());
            mailNotifCb.setValue(VaadinSessionUtils.getAttribute(UserPreferences.class).getMailNotifications());
            themeSelect.setValue(VaadinSessionUtils.getAttribute(UserPreferences.class).getThemeName());
        });

        addTab("Appearance", themeSelect);

        Button changePassword = new Button("Change Password" ,FontAwesome.EDIT);
        changePassword.addClickListener(event -> {
            FormWindow.show(new FormWindow.Config<PasswordChangeDTO>(FormWindow.Mode.NEW)
                    .setBeanClass(PasswordChangeDTO.class)
                    .setFormBean(new PasswordChangeDTO())
                    .setPropertyIdToFieldTypeMap(ImmutableMap.of(
                            "currentPassword", PasswordField.class,
                            "password", PasswordField.class,
                            "passwordConfirm", PasswordField.class
                    )).setCaptionToPropertyIdMap(ImmutableMap.of(
                            "Enter Current Password", "currentPassword",
                            "Enter New Password", "password",
                            "Confirm New Password", "passwordConfirm"
                    )).setOnCommitSuccess((Set<PasswordChangeDTO> set) -> {
                        if (set.iterator().hasNext()) {
                            userService.changePassword((PasswordChangeDTO) iterator().next());
                        }
                    }).setSuccessNotification("Password Change", "Password successfully changed")
                    .setCaption("Password Change - " + FormatUtils.bold(SecurityUtils.loggedInUserName())));
        });

        addTab("Security", changePassword);

        setContent(tabSheet);
        center();
    }

    private void configureTabsheet() {
        tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
    }

    private void addTab(String caption, Component... components) {
        FormLayout fl = new FormLayout(components);
        fl.setMargin(true);
        fl.setSizeFull();
        tabSheet.addTab(fl, caption);
    }

}
