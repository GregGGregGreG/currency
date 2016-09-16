package org.baddev.currency.exchanger.impl;

import org.baddev.currency.core.RoleEnum;
import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.exchanger.ExchangerService;
import org.baddev.currency.jooq.schema.Tables;
import org.baddev.currency.jooq.schema.tables.daos.ExchangeOperationDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.baddev.currency.jooq.schema.tables.records.ExchangeOperationRecord;
import org.joda.time.LocalDateTime;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@Service("exchanger")
public class ExchangerServiceImpl implements ExchangerService {

    private static final Logger log = LoggerFactory.getLogger(ExchangerServiceImpl.class);

    private ExchangeOperationDao exchangeDao;

    @Autowired
    public ExchangerServiceImpl(ExchangeOperationDao exchangeDao) {
        this.exchangeDao = exchangeDao;
    }

    @Override
    @Transactional
    public ExchangeOperation exchange(IExchangeOperation operation,
                                      Collection<? extends IExchangeRate> rates) {
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

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN})
    public Collection<? extends IExchangeOperation> findAll() {
        return exchangeDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public Collection<? extends IExchangeOperation> findForUser(Long key) {
        return exchangeDao.fetchByUserId(key);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN})
    public Collection<? extends IExchangeOperation> find(Long... ids) {
        return exchangeDao.fetchById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN})
    public IExchangeOperation findOne(Long id) {
        return exchangeDao.fetchOneById(id);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void delete(Long... ids) {
        exchangeDao.deleteById(ids);
    }

    private static <E extends IExchangeRate> Double filterRate(Collection<E> rates, String ccy) {
        return rates.stream()
                .filter(r -> r.getCcy().equals(ccy))
                .mapToDouble(IExchangeRate::getRate)
                .findFirst()
                .orElseThrow(ServiceException::new);
    }

}
