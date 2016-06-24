package org.baddev.currency.jooq.config;

import org.joda.time.LocalDate;
import org.jooq.Converter;

import java.sql.Date;

/**
 * Created by IPotapchuk on 4/4/2016.
 */
public class DateToLocalDateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate from(Date databaseObject) {
        return LocalDate.fromDateFields(databaseObject);
    }

    @Override
    public Date to(LocalDate userObject) {
        return new Date(userObject.toDate().getTime());
    }

    @Override
    public Class<Date> fromType() {
        return Date.class;
    }

    @Override
    public Class<LocalDate> toType() {
        return LocalDate.class;
    }
}
