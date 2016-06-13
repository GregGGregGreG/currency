package org.baddev.currency.ui;

import com.vaadin.data.util.converter.Converter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by IPOTAPCHUK on 6/13/2016.
 */
public class DoubleAmountToStringConverter implements Converter<String, Double> {

    @Override
    public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale) throws ConversionException {
        return Double.parseDouble(value.replaceAll("<[^>]+>", ""));
    }

    @Override
    public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        NumberFormat f = NumberFormat.getInstance(Locale.US);
        BigDecimal d = new BigDecimal(value);
        int fractions = d.precision();
        if (value % 2 == 0)
            f.setMaximumFractionDigits(1);
        else if (fractions > 8)
            f.setMaximumFractionDigits(8);
        return "<b>" + f.format(value) + "</b>";
    }

    @Override
    public Class<Double> getModelType() {
        return Double.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
