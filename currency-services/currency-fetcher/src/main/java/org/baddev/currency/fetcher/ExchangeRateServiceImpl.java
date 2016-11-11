package org.baddev.currency.fetcher;

import org.baddev.currency.core.api.ExchangeRateFetcher;
import org.baddev.currency.core.api.ExchangeRateService;
import org.baddev.currency.core.exception.RatesNotFoundException;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Currency;
import java.util.Optional;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateFetcher fetcher;
    private final ExtendedExchangeRateDao rateDao;

    @Autowired
    public ExchangeRateServiceImpl(ExchangeRateFetcher fetcher, ExtendedExchangeRateDao rateDao) {
        Assert.notNull(fetcher, "fetcher can't be null");
        Assert.notNull(rateDao, "rateDao can't be null");
        this.fetcher = fetcher;
        this.rateDao = rateDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<? extends IExchangeRate> findById(Long... ids) {
        return rateDao.fetchById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IExchangeRate> findOneById(Long id) {
        return Optional.ofNullable(rateDao.fetchOneById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<? extends IExchangeRate> findAll() {
        return rateDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<? extends IExchangeRate> findLast() {
        return rateDao.findLastRates();
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void delete(Long... ids) {
        rateDao.deleteById(ids);
    }

    @Override
    @Transactional
    public Collection<? extends IExchangeRate> fetchCurrent() throws RatesNotFoundException {
        try {
            Collection<? extends IExchangeRate> fetched = fetcher.fetchCurrent();
            if (fetched.isEmpty()) throw ratesNotFound(null);
            return fetched;
        } catch (RatesNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw ratesNotFound(e);
        }
    }

    private RatesNotFoundException ratesNotFound(Throwable cause) {
        return new RatesNotFoundException("Rates were not found", cause);
    }

    @Override
    @Transactional
    public Collection<? extends IExchangeRate> fetchByDate(LocalDate date) throws RatesNotFoundException {
        try {
            Collection<? extends IExchangeRate> fetched = fetcher.fetchByDate(date);
            if (fetched.isEmpty()) throw ratesNotFound(null);
            return fetched;
        } catch (RatesNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw ratesNotFound(e);
        }
    }

    @Override
    @Transactional
    public Optional<? extends IExchangeRate> fetchByCurrencyAndDate(Currency currency, LocalDate date) {
        return fetcher.fetchByCurrencyAndDate(currency, date);
    }
}
