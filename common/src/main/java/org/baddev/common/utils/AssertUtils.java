package org.baddev.common.utils;

import java.util.Arrays;

/**
 * Created by IPotapchuk on 11/10/2016.
 */
public final class AssertUtils {

    private AssertUtils() {
    }

    public static void notNull(Object object, String msg) {
        if (object == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void notNull(Object object) {
        notNull(object, "[Assertion Failed] - given object must be not null");
    }

    public static void objectsNotNull(Object...objects){
        notNull(objects);
        Arrays.stream(objects).forEach(AssertUtils::notNull);
    }

    public static void notEmpty(Object[] objects) {
        notNull(objects, "[Assertion Failed] - given array must be not null");
        if (objects.length == 0) {
            throw new IllegalArgumentException("[Assertion Failed] - given array must have at least 1 element");
        }
    }

}
