package org.baddev.currency.jooq.converter;

import org.jooq.Converter;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public class ByteToBooleanConverter implements Converter<Byte, Boolean> {

    @Override
    public Boolean from(Byte databaseObject) {
        return databaseObject.intValue() == 1;
    }

    @Override
    public Byte to(Boolean userObject) {
        return userObject ? Byte.parseByte("1") : Byte.parseByte("0");
    }

    @Override
    public Class<Byte> fromType() {
        return Byte.class;
    }

    @Override
    public Class<Boolean> toType() {
        return Boolean.class;
    }
}
