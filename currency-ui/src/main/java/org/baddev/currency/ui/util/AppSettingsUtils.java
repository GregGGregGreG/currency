package org.baddev.currency.ui.util;

import com.vaadin.server.VaadinSession;
import org.baddev.currency.mail.ExchangeCompletionMailer;
import org.baddev.currency.notifier.listener.NotificationListener;
import org.baddev.currency.ui.MyUI;

import static org.baddev.currency.ui.MyUI.myUI;
import static org.baddev.currency.ui.util.SessionAttribute.NOTIFICATION_MAIL_ATTR;
import static org.baddev.currency.ui.util.SessionAttribute.NOTIFICATION_UI_ATTR;

/**
 * Created by IPotapchuk on 6/23/2016.
 */
public final class AppSettingsUtils {

    private AppSettingsUtils(){}

    public static void toggleUINotifications(boolean enabled) {
        setNotificationFlag(NOTIFICATION_UI_ATTR, enabled);
        VaadinSession.getCurrent().getUIs().forEach(ui -> {
            if (ui instanceof MyUI) {
                if (enabled)
                    ((MyUI) ui).registerListener((NotificationListener) ui);
                else ((MyUI) ui).unregisterListener((NotificationListener) ui);
            }
        });
    }

    public static void toggleMailNotifications(boolean enabled, ExchangeCompletionMailer mailListener) {
        setNotificationFlag(NOTIFICATION_MAIL_ATTR, true);
        if (enabled)
            myUI().registerListener(mailListener);
        else myUI().unregisterListener(mailListener);
    }

    private static void setNotificationFlag(String attr, boolean flag) {
        VaadinSession.getCurrent().setAttribute(attr, flag);
    }

}
