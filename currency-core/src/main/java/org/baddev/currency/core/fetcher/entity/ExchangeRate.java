package org.baddev.currency.core.fetcher.entity;

import org.baddev.currency.core.Identity;
import org.joda.time.LocalDate;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public interface ExchangeRate extends Identity<Long> {

    String getBaseLiterCode();

    String getLiterCode();

    LocalDate getExchangeDate();

    Double getRate();

}
