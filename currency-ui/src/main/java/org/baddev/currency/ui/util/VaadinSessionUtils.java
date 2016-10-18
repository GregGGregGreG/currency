package org.baddev.currency.ui.util;

import com.vaadin.server.VaadinSession;

/**
 * Created by IPotapchuk on 7/1/2016.
 */
public final class VaadinSessionUtils {

    private VaadinSessionUtils() {
    }

    public static VaadinSession getSession() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null)
            throw new IllegalStateException("No vaadin session bound to the current thread");
        return session;
    }

    public static Object getAttribute(String name) {
        Object attr = getSession().getAttribute(name);
        if (attr == null)
            throw new IllegalArgumentException("Can't find required session attribute: " + name);
        return attr;
    }

    public static <T> T getAttribute(Class<T> clazz) {
        T attr = getSession().getAttribute(clazz);
        if (attr == null)
            throw new IllegalArgumentException("Can't find required session attribute: " + clazz.getName());
        return attr;
    }

    public static boolean isAttributeExist(Class clazz){
        try {
            getAttribute(clazz);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAttributeExist(String name){
        try {
            getAttribute(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <T> void setAttribute(Class<T> type, T value){
        getSession().setAttribute(type, value);
    }

}
