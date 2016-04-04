package org.baddev.currency.ui.currency.dao.utils;

import org.joda.time.LocalDate;

import java.sql.Date;
import java.util.Objects;

/**
 * Created by IPotapchuk on 3/15/2016.
 */
public final class ConverterUtils {

    private ConverterUtils() {
    }

    public static Date toSqlDate(LocalDate date) {
        check(date);
        return new Date(date.toDate().getTime());
    }

    public static LocalDate fromSqlDate(Date date) {
        check(date);
        return new LocalDate(date.getTime());
    }

    private static void check(Object obj) {
        Objects.requireNonNull(obj, "Date can't be null");
    }

}
