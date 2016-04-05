package org.baddev.currency.exchange.impl;

import org.baddev.currency.core.exchange.entity.ExchangeOperation;
import org.baddev.currency.core.exchange.exception.CurrencyNotFoundException;
import org.baddev.currency.core.exchange.job.Exchanger;
import org.baddev.currency.core.fetcher.entity.ExchangeRate;
import org.baddev.currency.dao.exchange.ExchangeOperationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@Service("exchanger")
public class ExchangerImpl implements Exchanger {

    private static final Logger log = LoggerFactory.getLogger(ExchangerImpl.class);

    @Autowired
    private ExchangeOperationDao exchangeDao;

    @Transactional
    @SuppressWarnings("unchecked")
    public double exchange(ExchangeOperation operation, Collection<ExchangeRate> rates)
            throws CurrencyNotFoundException {
        double excAmount = operation.exchange(rates);
        log.info("Exchanged amount: [{}]{}", excAmount,
                operation.getExchangedAmountLiterCode());
        exchangeDao.save(operation);
        return excAmount;
    }
}
