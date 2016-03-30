package org.baddev.currency.exchange.impl.nbu;

import org.baddev.currency.exchange.job.ExchangeJob;
import org.baddev.currency.exchange.dao.ExchangeOperationDao;
import org.baddev.currency.exchange.entity.ExchangeOperation;
import org.baddev.currency.exchange.exception.CurrencyNotFoundException;
import org.baddev.currency.fetcher.entity.ExchangeRate;
import org.baddev.currency.fetcher.ExchangeRateFetcher;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@Service("NBUCounter")
public class NBUScheduledExchanger implements ExchangeJob {

    private static final Logger log = LoggerFactory.getLogger(NBUScheduledExchanger.class);

    @NBU
    private ExchangeRateFetcher fetcher;

    @Autowired
    private ExchangeOperationDao dao;

    @Scheduled(cron = "${perminute}")
    @SuppressWarnings("unchecked")
    public void exchange() throws CurrencyNotFoundException {
        List<ExchangeRate> rates = (List<ExchangeRate>) fetcher.fetchCurrent();
        ExchangeOperation record = ExchangeOperation.newBuilder()
                .amount(400)
                .from(Currency.getInstance(Locale.US).getCurrencyCode())
                .to(Currency.getInstance("UAH").getCurrencyCode())
                .date(new LocalDate())
                .build();
        record.exchange(rates);
        log.info("Exchanged amount: [{}]{}", record.getExchangedAmount(), record.getExchangedAmountLiterCode());
        dao.save(record);
    }
}
