package org.baddev.currency.ui.listener.eventbus;

import com.google.common.eventbus.Subscribe;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.ui.CurrencyUI;
import org.baddev.currency.ui.event.binder.AppearanceChangeEvent;
import org.baddev.currency.ui.event.binder.NotificationChangeEvent;
import org.baddev.currency.ui.event.binder.SecurityChangeEvent;
import org.baddev.currency.ui.util.VaadinSessionUtils;

import java.io.Serializable;

/**
 * Created by IPotapchuk on 11/18/2016.
 */
@SpringComponent
@UIScope
public class SettingsChangeListener implements Serializable {

    private static final long serialVersionUID = -6250093111234909865L;

    @Subscribe
    private void notificationSettingsChange(NotificationChangeEvent event) {
        event.process(notifChangedEvent -> {
            VaadinSessionUtils.alterAttribute(UserPreferences.class, pr -> {
                pr.setUiNotifications(notifChangedEvent.getEventData().isUiNotifOnExchangeTaskCompletion());
                pr.setMailNotifications(notifChangedEvent.getEventData().isMailOnExchangeTaskCompletion());
            });
            CurrencyUI.get().applyNotificationPreferences();
        });
    }

    @Subscribe
    private void appearanceSettingsChange(AppearanceChangeEvent event) {
        event.process(appearanceChangedEvent -> {
            String newTheme = appearanceChangedEvent.getEventData().getThemeName();
            if (!newTheme.equals(CurrencyUI.getCurrent().getTheme())) {
                VaadinSessionUtils.getAttribute(UserPreferences.class).setThemeName(newTheme);
                VaadinSessionUtils.getSession().getUIs().forEach(ui -> {
                    ui.setTheme(VaadinSessionUtils.getAttribute(UserPreferences.class).getThemeName());
                });
            }
        });
    }

    @Subscribe
    private void securitySettingsChange(SecurityChangeEvent event) {
        event.process(securityChangedEvent -> VaadinSessionUtils.alterAttribute(UserPreferences.class, pr -> {
            pr.setEmailSignIn(securityChangedEvent.getEventData().isSignInWithEmail());
            pr.setTwoFactorAuth(securityChangedEvent.getEventData().isTwoFactorAuth());
        }));
    }

}
