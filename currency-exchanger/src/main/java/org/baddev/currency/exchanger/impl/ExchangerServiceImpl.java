package org.baddev.currency.exchanger.impl;

import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.exchanger.ExchangerService;
import org.baddev.currency.exchanger.ExtendedExchangeOperationDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.joda.time.LocalDateTime;
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
public class ExchangerServiceImpl implements ExchangerService<ExchangeOperation, ExchangeRate> {

    private static final Logger log = LoggerFactory.getLogger(ExchangerServiceImpl.class);

    @Autowired
    private ExtendedExchangeOperationDao exchangeDao;

    @Transactional
    public ExchangeOperation exchange(ExchangeOperation operation,
                                      Collection<ExchangeRate> rates) {
        operation.setPerformDatetime(LocalDateTime.now());
        if (operation.getFromCcy().equals("UAH")) {
            double rate = findRate(rates, operation.getToCcy());
            operation.setToAmount(rate * operation.getFromAmount());
        } else {
            double fPairRate = findRate(rates, operation.getFromCcy());
            double inDefaultBase = fPairRate * operation.getFromAmount();
            double sPairRate = findRate(rates, operation.getToCcy());
            operation.setToAmount(inDefaultBase / sPairRate);
        }
        log.info("Exchanged amount: [{}]{}", operation.getToAmount(),
                operation.getToCcy());

        return exchangeDao.insertReturning(operation);
    }

    private static <E extends IExchangeRate> Double findRate(Collection<E> rates, String ccy) {
        return rates.stream()
                .filter(r -> r.getCcy().equals(ccy))
                .mapToDouble(IExchangeRate::getRate)
                .findFirst()
                .orElseThrow(ServiceException::new);
    }

}
