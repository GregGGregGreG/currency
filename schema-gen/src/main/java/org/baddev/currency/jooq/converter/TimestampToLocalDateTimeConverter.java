package org.baddev.currency.jooq.converter;

import org.joda.time.LocalDateTime;
import org.jooq.Converter;

import java.sql.Timestamp;

/**
 * Created by IPotapchuk on 6/24/2016.
 */
public class TimestampToLocalDateTimeConverter implements Converter<Timestamp, LocalDateTime> {

    @Override
    public LocalDateTime from(Timestamp databaseObject) {
        return new LocalDateTime(databaseObject.getTime());
    }

    @Override
    public Timestamp to(LocalDateTime userObject) {
        return new Timestamp(userObject.toDate().getTime());
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<LocalDateTime> toType() {
        return LocalDateTime.class;
    }
}
