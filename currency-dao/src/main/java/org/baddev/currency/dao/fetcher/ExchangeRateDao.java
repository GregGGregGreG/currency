package org.baddev.currency.dao.fetcher;

import org.baddev.currency.dao.GenericDao;
import org.baddev.currency.fetcher.entity.ExchangeRate;

import java.util.Collection;

/**
 * Created by IPotapchuk on 4/4/2016.
 */
public interface ExchangeRateDao extends GenericDao<ExchangeRate> {

    void saveAll(Collection<ExchangeRate> rates);

}
