package org.baddev.currency.dao.fetcher;

import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.dao.GenericDao;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;

/**
 * Created by IPotapchuk on 4/4/2016.
 */
public interface ExchangeRateDao extends GenericDao<BaseExchangeRate> {

    void saveAll(Collection<BaseExchangeRate> rates);

    List<BaseExchangeRate> findForCcy(String ccy);

    List<BaseExchangeRate> findForDate(LocalDate date);

}
