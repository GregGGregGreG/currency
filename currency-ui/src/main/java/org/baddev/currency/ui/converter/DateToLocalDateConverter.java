package org.baddev.currency.ui.converter;

import com.vaadin.data.util.converter.Converter;
import org.joda.time.LocalDate;

import java.util.Date;
import java.util.Locale;

/**
 * Created by IPotapchuk on 6/24/2016.
 */
public class DateToLocalDateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate convertToModel(Date value, Class<? extends LocalDate> targetType, Locale locale) throws ConversionException {
        if (value == null) return null;
        return LocalDate.fromDateFields(value);
    }

    @Override
    public Date convertToPresentation(LocalDate value, Class<? extends Date> targetType, Locale locale) throws ConversionException {
        if(value == null) return null;
        return value.toDate();
    }

    @Override
    public Class<LocalDate> getModelType() {
        return LocalDate.class;
    }

    @Override
    public Class<Date> getPresentationType() {
        return Date.class;
    }

}
