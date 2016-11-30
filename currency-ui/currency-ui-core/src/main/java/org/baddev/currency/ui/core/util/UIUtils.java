package org.baddev.currency.ui.core.util;

import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HasComponents;

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

    private static Component findComponent(HasComponents root, Component component) {
        for (Component child : root) {
            if (component.equals(child)) {
                return child;
            } else if (child instanceof HasComponents) {
                return findComponent((HasComponents) child, component);
            }
        }
        return null;
    }

    public static <T extends Component> T findComponent(HasComponents root, Class<T> clazz) {
        for(Component child : root){
            if (clazz.isInstance(child)) {
                return (T)child;
            } else if (child instanceof HasComponents) {
                return findComponent((HasComponents) child, clazz);
            }
        }
        return null;
    }

    public static boolean isComponentExists(HasComponents root, Component component) {
        return findComponent(root, component) != null;
    }

}
