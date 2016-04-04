package org.baddev.currency.ui.currency.jooq.config;

import org.jooq.Converter;

import java.time.LocalDate;

/**
 * Created by IPotapchuk on 4/4/2016.
 */
public class LocalDateConverter implements Converter<Long, LocalDate> {

    @Override
    public LocalDate from(Long databaseObject) {
        return LocalDate.ofEpochDay(databaseObject);
    }

    @Override
    public Long to(LocalDate userObject) {
        return userObject.toEpochDay();
    }

    @Override
    public Class<Long> fromType() {
        return Long.class;
    }

    @Override
    public Class<LocalDate> toType() {
        return LocalDate.class;
    }
}
