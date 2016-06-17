package org.baddev.currency.ui;

import java.util.List;

/**
 * Created by IPOTAPCHUK on 6/15/2016.
 */
public final class FormatUtils {

    private FormatUtils() {
    }

    public static String formatCcyNamesList(List<String> ccyNames){
        if (ccyNames.isEmpty())
            return "Unknown";
        StringBuilder sb = new StringBuilder();
        ccyNames.forEach(s -> sb.append(s).append(", "));
        sb.delete(sb.toString().length() - 2, sb.toString().length() - 1);
        return sb.toString();
    }

}
