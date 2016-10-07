package org.baddev.currency.ui.util;

import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

import java.util.Arrays;

/**
 * Created by IPOTAPCHUK on 6/13/2016.
 */
public final class UIUtils {

    private UIUtils() {
    }

    public static boolean isAllValid(Field... f) {
        return Arrays.stream(f).allMatch(fd -> fd.getValue() != null && fd.isValid());
    }

    public static void toggleVisible(boolean visible, Component... components) {
        Arrays.stream(components).forEach(c -> c.setVisible(visible));
    }

    public static void toggleEnabled(boolean enabled, Component... components) {
        Arrays.stream(components).forEach(c -> c.setEnabled(enabled));
    }

}
