package org.baddev.currency.ui.util;

import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.DateRenderer;

import java.text.DateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IPOTAPCHUK on 6/15/2016.
 */
public final class FormatUtils {

    private FormatUtils() {
    }

    public static DateFormat date() {
        return UI.getCurrent() != null ?
                DateFormat.getDateInstance(DateFormat.DATE_FIELD, UI.getCurrent().getLocale()) :
                DateFormat.getDateInstance(DateFormat.DATE_FIELD);
    }

    public static DateFormat dateTime() {
        return UI.getCurrent() != null ?
                DateFormat.getDateTimeInstance(DateFormat.DATE_FIELD, DateFormat.MEDIUM, UI.getCurrent().getLocale()) :
                DateFormat.getDateTimeInstance(DateFormat.DATE_FIELD, DateFormat.MEDIUM);
    }

    public static DateRenderer dateRenderer(boolean dateTime) {
        return new DateRenderer(dateTime ? dateTime() : date(), "");
    }

    public static String joinByComma(List<String> paramValues) {
        return joinByCommaWithDefVal(paramValues, "Unknown");
    }

    public static String joinByCommaWithDefVal(List<String> paramValues, String defVal) {
        return (paramValues == null || paramValues.isEmpty()) ? defVal : paramValues.stream().collect(Collectors.joining(", "));
    }

    public static String bold(Object text) {
        return text != null ? "<b>" + text.toString() + "</b>" : "";
    }

    public static String boldInQuotes(String text) {
        return "\"" + bold(text) + "\"";
    }

    public static String exchangeNotification(double fromAmount, String fromCcyNames, String fromCcy, double toAmount, String toCcyNames, String toCcy) {
        return String.format("%.2f %s(%s) <> %.2f %s(%s)",
                fromAmount,
                fromCcyNames,
                fromCcy,
                toAmount,
                toCcyNames,
                toCcy);
    }

}
