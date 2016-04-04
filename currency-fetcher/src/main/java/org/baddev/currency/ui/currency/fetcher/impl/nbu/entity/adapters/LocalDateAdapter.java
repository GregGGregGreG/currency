package org.baddev.currency.ui.currency.fetcher.impl.nbu.entity.adapters;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    private DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy");

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return fmt.parseLocalDate(v);
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return v.toString(fmt);
    }

}
