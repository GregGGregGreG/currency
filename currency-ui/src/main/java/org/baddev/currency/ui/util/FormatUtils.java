package org.baddev.currency.ui.util;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IPOTAPCHUK on 6/15/2016.
 */
public final class FormatUtils {

    private FormatUtils() {
    }

    public static String joinByComma(List<String> paramValues) {
        return joinByCommaWithDefVal(paramValues, "Unknown");
    }

    public static String joinByCommaWithDefVal(List<String> paramValues, String defVal) {
        return (paramValues == null || paramValues.isEmpty()) ? defVal : paramValues.stream().collect(Collectors.joining(", "));
    }

    public static String bold(String text) {
        return "<b>" + text + "</b>";
    }

    public static String boldInQuotes(String text) {
        return "\"" + bold(text) + "\"";
    }

}
