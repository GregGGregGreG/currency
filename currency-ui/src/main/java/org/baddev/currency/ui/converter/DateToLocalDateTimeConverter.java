package org.baddev.currency.ui.converter;

import com.vaadin.data.util.converter.Converter;
import org.joda.time.LocalDateTime;

import java.util.Date;
import java.util.Locale;

/**
 * Created by IPotapchuk on 6/24/2016.
 */
public class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {

    @Override
    public LocalDateTime convertToModel(Date value, Class<? extends LocalDateTime> targetType, Locale locale) throws ConversionException {
        if (value == null) return null;
        return LocalDateTime.fromDateFields(value);
    }

    @Override
    public Date convertToPresentation(LocalDateTime value, Class<? extends Date> targetType, Locale locale) throws ConversionException {
        if (value == null) return null;
        return value.toDate();
    }

    @Override
    public Class<LocalDateTime> getModelType() {
        return LocalDateTime.class;
    }

    @Override
    public Class<Date> getPresentationType() {
        return Date.class;
    }

}
