package org.baddev.currency.core.fetcher.entity;

import org.baddev.currency.core.MutableIdentity;
import org.joda.time.LocalDate;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface ExchangeRate extends MutableIdentity<Long> {

    String getBaseCcy();

    String getCcy();

    LocalDate getDate();

    Double getRate();

}
