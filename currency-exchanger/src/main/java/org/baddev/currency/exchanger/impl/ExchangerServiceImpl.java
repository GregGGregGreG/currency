package org.baddev.currency.exchanger.impl;

import org.baddev.currency.core.RoleEnum;
import org.baddev.currency.exchanger.ExchangeAction;
import org.baddev.currency.exchanger.ExchangerService;
import org.baddev.currency.jooq.schema.tables.daos.ExchangeOperationDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@Service("exchanger")
public class ExchangerServiceImpl implements ExchangerService {

    private static final Logger log = LoggerFactory.getLogger(ExchangerServiceImpl.class);

    private ExchangeOperationDao exchangeDao;
    private ExchangeAction exchangeAction;

    @Autowired
    public ExchangerServiceImpl(ExchangeOperationDao exchangeDao, ExchangeAction exchangeAction) {
        this.exchangeDao = exchangeDao;
        this.exchangeAction = exchangeAction;
    }

    @Override
    @Transactional
    public IExchangeOperation exchange(IExchangeOperation operation,
                                      Collection<? extends IExchangeRate> rates) {
        return exchangeAction.exchange(operation, rates);
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
    public Collection<? extends IExchangeOperation> findById(Long... ids) {
        return exchangeDao.fetchById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN})
    public Optional<IExchangeOperation> findOneById(Long id) {
        return Optional.ofNullable(exchangeDao.fetchOneById(id));
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void delete(Long... ids) {
        exchangeDao.deleteById(ids);
    }



}
