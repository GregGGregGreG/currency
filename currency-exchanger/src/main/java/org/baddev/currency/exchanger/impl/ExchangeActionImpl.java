package org.baddev.currency.exchanger.impl;

import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.exchanger.ExchangeAction;
import org.baddev.currency.jooq.schema.Tables;
import org.baddev.currency.jooq.schema.tables.daos.ExchangeRateDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.baddev.currency.jooq.schema.tables.records.ExchangeOperationRecord;
import org.joda.time.LocalDateTime;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by IPotapchuk on 9/30/2016.
 */
@Component
public class ExchangeActionImpl implements ExchangeAction{

    private static final Logger log = LoggerFactory.getLogger(ExchangeActionImpl.class);

    @Autowired
    private ExchangeRateDao exchangeDao;

    @Override
    public IExchangeOperation exchange(IExchangeOperation operation, Collection<? extends IExchangeRate> rates) {
        Objects.requireNonNull(operation);
        ExchangeOperation op = operation.into(new ExchangeOperation());
        op.setPerformDatetime(LocalDateTime.now());
        if (op.getFromCcy().equals("UAH")) {
            double rate = filterRate(rates, op.getToCcy());
            op.setToAmount(rate * op.getFromAmount());
        } else {
            double fPairRate = filterRate(rates, op.getFromCcy());
            double inDefaultBase = fPairRate * op.getFromAmount();
            double sPairRate = filterRate(rates, op.getToCcy());
            op.setToAmount(inDefaultBase / sPairRate);
        }
        log.info("Exchanged amount: [{}]{}, userId=[{}]", op.getToAmount(), op.getToCcy(), op.getUserId());
        return DSL.using(exchangeDao.configuration())
                .insertInto(Tables.EXCHANGE_OPERATION)
                .set(op.into(new ExchangeOperationRecord()))
                .returning()
                .fetchOne()
                .into(ExchangeOperation.class);
    }

    private static <E extends IExchangeRate> Double filterRate(Collection<E> rates, String ccy) {
        return rates.stream()
                .filter(r -> r.getCcy().equals(ccy))
                .mapToDouble(IExchangeRate::getRate)
                .findFirst()
                .orElseThrow(ServiceException::new);
    }
}
