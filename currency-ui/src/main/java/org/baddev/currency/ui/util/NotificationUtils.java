package org.baddev.currency.ui.util;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by IPotapchuk on 7/2/2016.
 */
public class NotificationUtils {

    public static final int DEFAULT_DELAY = 3500;

    public static void notifySuccess(String caption, String message) {
        notifyCustom(caption, message, DEFAULT_DELAY, Position.BOTTOM_CENTER,
                ValoTheme.NOTIFICATION_SUCCESS);
    }

    public static void notifySuccessCloseable(String caption, String message) {
        notifyCustom(caption, message, -1, Position.BOTTOM_CENTER,
                ValoTheme.NOTIFICATION_SUCCESS,
                ValoTheme.NOTIFICATION_CLOSABLE);
    }

    public static void notifyFailure(String caption, String message) {
        notifyCustom(caption, message, -1, Position.TOP_CENTER,
                ValoTheme.NOTIFICATION_FAILURE,
                ValoTheme.NOTIFICATION_CLOSABLE);
    }

    public static void notifyWarn(String caption, String message){
        notifyCustom(caption, message, -1, Position.TOP_CENTER,
                ValoTheme.NOTIFICATION_WARNING,
                ValoTheme.NOTIFICATION_CLOSABLE);
    }

    public static void notifyTray(String caption, String message) {
        notifyCustom(caption, message, DEFAULT_DELAY, Position.BOTTOM_RIGHT,
                ValoTheme.NOTIFICATION_TRAY,
                ValoTheme.NOTIFICATION_SMALL);
    }

    public static void notifyCustom(String caption, String message, int delay, Position position, String... styles) {
        Notification n = new Notification(caption, message);
        n.setHtmlContentAllowed(true);
        if (styles != null && styles.length > 0) {
            if (styles.length == 1) {
                n.setStyleName(styles[0]);
            } else {
                StringBuilder sb = new StringBuilder();
                Arrays.asList(styles).forEach(s -> sb.append(s).append(" "));
                n.setStyleName(sb.toString());
            }
        }
        if (position != null) n.setPosition(position);
        n.setDelayMsec(delay);
        Page page = Page.getCurrent();
        Objects.requireNonNull(page, "No UI was found");
        n.show(page);
    }

}
