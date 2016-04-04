package org.baddev.currency.exchange.impl.nbu;

import org.baddev.currency.dao.exchange.ExchangeOperationDao;
import org.baddev.currency.dao.fetcher.ExchangeRateDao;
import org.baddev.currency.exchange.entity.ExchangeOperation;
import org.baddev.currency.exchange.exception.CurrencyNotFoundException;
import org.baddev.currency.exchange.job.ExchangeJob;
import org.baddev.currency.fetcher.ExchangeRateFetcher;
import org.baddev.currency.fetcher.entity.ExchangeRate;
import org.baddev.currency.fetcher.impl.nbu.NBU;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@Service("NBUExchanger")
public class NBUScheduledExchanger implements ExchangeJob {

    private static final Logger log = LoggerFactory.getLogger(NBUScheduledExchanger.class);

    @NBU
    private ExchangeRateFetcher fetcher;

    @Autowired
    private ExchangeOperationDao exchangeDao;

    @Autowired
    private ExchangeRateDao rateDao;

    @Scheduled(cron = "${perminute}")
    @Transactional
    @SuppressWarnings("unchecked")
    public void exchange() throws CurrencyNotFoundException {
        List<ExchangeRate> rates = (List<ExchangeRate>) fetcher.fetchCurrent();
        ExchangeOperation exchangeOperation = ExchangeOperation.newBuilder()
                .amount(400)
                .from(Currency.getInstance(Locale.US).getCurrencyCode())
                .to(Currency.getInstance("UAH").getCurrencyCode())
                .date(new LocalDate())
                .build();
        exchangeOperation.exchange(rates);
        log.info("Exchanged amount: [{}]{}", exchangeOperation.getExchangedAmount(),
                exchangeOperation.getExchangedAmountLiterCode());
        rateDao.saveAll(rates);
        exchangeDao.save(exchangeOperation);
    }
}
