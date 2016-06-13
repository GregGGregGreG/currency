package org.baddev.currency.ui.validation;

import com.vaadin.ui.Field;

import java.util.Arrays;

/**
 * Created by IPOTAPCHUK on 6/13/2016.
 */
public final class ViewValidation {

    private ViewValidation() {
    }

    public static boolean isValid(Field... f) {
        return Arrays.stream(f).allMatch(fd -> fd.isValid() && fd.getValue() != null);
    }

}
