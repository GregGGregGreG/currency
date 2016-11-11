package org.baddev.common.utils;

/**
 * Created by IPotapchuk on 11/10/2016.
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

}
