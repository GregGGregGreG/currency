package org.baddev.currency.exchanger.impl;

import org.baddev.currency.core.exchanger.Exchanger;
import org.baddev.currency.core.exchanger.entity.ExchangeOperation;
import org.baddev.currency.core.fetcher.entity.ExchangeRate;
import org.baddev.currency.dao.exchanger.ExchangeOperationDao;
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
    public ExchangeOperation exchange(ExchangeOperation operation, Collection<ExchangeRate> rates) {
        operation.exchange(rates);
        log.info("Exchanged amount: [{}]{}", operation.getExchangedAmount(),
                operation.getToCcy());
        exchangeDao.save(operation);
        return operation;
    }
}
