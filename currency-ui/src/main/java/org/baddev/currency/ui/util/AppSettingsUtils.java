package org.baddev.currency.ui.util;

import org.baddev.currency.core.listener.NotificationListener;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.mail.ExchangeCompletionMailer;
import org.baddev.currency.ui.CurrencyUI;

import static org.baddev.currency.ui.CurrencyUI.currencyUI;
import static org.baddev.currency.ui.util.VaadinSessionUtils.getSession;
import static org.baddev.currency.ui.util.VaadinSessionUtils.getSessionAttribute;

/**
 * Created by IPotapchuk on 6/23/2016.
 */
public final class AppSettingsUtils {

    private AppSettingsUtils() {
    }

    private static void toggleUINotifications(boolean enabled) {
        getSession().getUIs().forEach(ui -> {
            if (ui instanceof CurrencyUI) {
                if (enabled)
                    ((CurrencyUI) ui).registerListener((NotificationListener) ui);
                else ((CurrencyUI) ui).unregisterListener((NotificationListener) ui);
            }
        });
    }

    private static void toggleMailNotifications(boolean enabled, ExchangeCompletionMailer mailListener) {
        if (enabled)
            currencyUI().registerListener(mailListener);
        else currencyUI().unregisterListener(mailListener);
    }

    public static void setupUserNotifications(ExchangeCompletionMailer mailer) {
        toggleUINotifications(getSessionAttribute(UserPreferences.class).getUiNotifications());
        toggleMailNotifications(getSessionAttribute(UserPreferences.class).getMailNotifications(), mailer);
    }

    public static void applyUserPreferences(ExchangeCompletionMailer mailer) {
        applyUISharedUserTheme();
        setupUserNotifications(mailer);
    }

    public static void applyUISharedUserTheme() {
        getSession().getUIs().forEach(ui -> {
            ui.access(() -> ui.setTheme(getSessionAttribute(UserPreferences.class).getThemeName()));
        });
    }

}
