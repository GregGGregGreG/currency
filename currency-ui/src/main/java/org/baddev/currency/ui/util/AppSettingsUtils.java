package org.baddev.currency.ui.util;

import com.vaadin.server.AbstractClientConnector;
import org.baddev.currency.mail.ExchangeCompletionMailer;
import org.baddev.currency.notifier.listener.NotificationListener;
import org.baddev.currency.ui.CurrencyUI;

import static org.baddev.currency.ui.CurrencyUI.currencyUI;
import static org.baddev.currency.ui.util.SessionAttribute.NTF_MAIL_ATTR;
import static org.baddev.currency.ui.util.SessionAttribute.NTF_UI_ATTR;
import static org.baddev.currency.ui.util.VaadinSessionUtils.*;

/**
 * Created by IPotapchuk on 6/23/2016.
 */
public final class AppSettingsUtils {

    private AppSettingsUtils() {
    }

    public static void initializeSettings(){
        if(!getSession().getUIs().stream().anyMatch(AbstractClientConnector::isAttached)) {
            setSessionAttributes(false, SessionAttribute.NTF_MAIL_ATTR, SessionAttribute.NTF_UI_ATTR);
        }
    }

    public static void toggleUINotifications(boolean enabled) {
        setSessionAttributes(enabled, NTF_UI_ATTR);
        getSession().getUIs().forEach(ui -> {
            if (ui instanceof CurrencyUI) {
                if (enabled)
                    ((CurrencyUI) ui).registerListener((NotificationListener) ui);
                else ((CurrencyUI) ui).unregisterListener((NotificationListener) ui);
            }
        });
    }

    public static boolean isUINotificationsEnabled() {
        return isNotificationEnabled(SessionAttribute.NTF_UI_ATTR);
    }

    public static boolean isMailNotificationEnabled() {
        return isNotificationEnabled(SessionAttribute.NTF_MAIL_ATTR);
    }

    private static boolean isNotificationEnabled(String name) {
        return (Boolean) getSessionAttribute(name);
    }

    public static void toggleMailNotifications(boolean enabled, ExchangeCompletionMailer mailListener) {
        setSessionAttributes(enabled, NTF_MAIL_ATTR);
        if (enabled)
            currencyUI().registerListener(mailListener);
        else currencyUI().unregisterListener(mailListener);
    }

}
