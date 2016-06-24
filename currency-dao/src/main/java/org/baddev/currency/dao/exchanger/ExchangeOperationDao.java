package org.baddev.currency.dao.exchanger;


import org.baddev.currency.core.exchanger.entity.ExchangeOperation;
import org.baddev.currency.dao.GenericDao;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.Collection;

/**
 * Created by IPotapchuk on 3/15/2016.
 */
public interface ExchangeOperationDao extends GenericDao<ExchangeOperation> {
    Collection<ExchangeOperation> loadByRatesDate(LocalDate date);
    Collection<ExchangeOperation> loadByPerformDateTime(LocalDateTime dateTime);
}
