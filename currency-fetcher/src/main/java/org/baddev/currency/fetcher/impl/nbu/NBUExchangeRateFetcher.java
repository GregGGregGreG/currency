package org.baddev.currency.fetcher.impl.nbu;

import org.apache.cxf.jaxrs.client.WebClient;
import org.baddev.currency.core.fetcher.ExchangeRateFetcher;
import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.dao.fetcher.ExchangeRateDao;
import org.baddev.currency.fetcher.impl.nbu.entity.NBUExchange;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Currency;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@Service("NBU")
@Transactional(noRollbackFor = NBUExchangeRateFetcher.NoRatesLocallyFoundException.class)
public class NBUExchangeRateFetcher implements ExchangeRateFetcher<BaseExchangeRate> {

    private static final Logger            log = LoggerFactory.getLogger(NBUExchangeRateFetcher.class);
    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

    @Value("${param_date_nbu}")     private String dateParam;
    @Value("${param_currency_nbu}") private String currencyParam;

    @Resource(name = "NBUClient")
    private WebClient client;

    @Autowired
    private ExchangeRateDao rateDao;


    public static final class NoRatesLocallyFoundException extends RuntimeException{
        //nothing to add
    }

    public static final class RatesFetchingError extends RuntimeException{
        public RatesFetchingError(String message) {
            super(message);
        }
    }

    private Collection<BaseExchangeRate> searchLocally(LocalDate date){
        Collection<BaseExchangeRate> localRates = rateDao.loadByDate(date);
        if(!localRates.isEmpty()) {
            log.info("{} rates found locally.", localRates.size());
            return localRates;
        }
        log.info("No rates found locally. Going to fetch by date {}", date.toString());
        throw new NoRatesLocallyFoundException();
    }

    @Override
    public Collection<BaseExchangeRate> fetchCurrent() {
        Collection<BaseExchangeRate> rates = null;
        try {
            rates = searchLocally(new LocalDate());
        } catch (NoRatesLocallyFoundException ex){
            rates = convert(client
                    .accept(MediaType.TEXT_XML_TYPE)
                    .get(NBUExchange.class));
            rateDao.saveAll(rates);
        }
        return rates;
    }

    @Override
    public Collection<BaseExchangeRate> fetchByDate(LocalDate date) {
        Collection<BaseExchangeRate> rates = null;
        try {
            rates = searchLocally(date);
        } catch (NoRatesLocallyFoundException ex){
            rates = convert(client
                    .accept(MediaType.TEXT_XML_TYPE)
                    .query(dateParam, date.toString(fmt))
                    .get(NBUExchange.class));
            rateDao.saveAll(rates);
        }
        return rates;
    }

    @Override
    public BaseExchangeRate fetchByCurrencyAndDate(Currency currency, LocalDate date) {
        BaseExchangeRate rate = null;
        try {
            rate = filter(currency, searchLocally(date));
        } catch (NoRatesLocallyFoundException ex) {
            rate =  convert(client
                    .accept(MediaType.TEXT_XML_TYPE)
                    .query(currencyParam, currency.getCurrencyCode())
                    .query(dateParam, date.toString(fmt))
                    .get(NBUExchange.class))
                    .iterator().next();
            rateDao.save(rate);
        }
        return rate;
    }

    private BaseExchangeRate filter(Currency currency, Collection<BaseExchangeRate> rates){
        return rates.stream()
                .filter(rate -> rate.getCcy().equals(currency.getCurrencyCode()))
                .findFirst()
                .orElseThrow(NoRatesLocallyFoundException::new);
    }

    private Collection<BaseExchangeRate> convert(NBUExchange exchange){
        if(exchange.getExchangeRates()==null)
            throw new RatesFetchingError("Fetching error. No rates available");
        log.info("Fetched and extracted [{}] records", exchange.getExchangeRates().size());
        return exchange.getExchangeRates().stream().map(r -> BaseExchangeRate.newBuilder()
                .baseCurrencyCode(r.getBaseCurrencyCode())
                .currencyCode(r.getCcy())
                .date(r.getDate())
                .rate(r.getRate())
                .build()).collect(Collectors.toList());
    }

}
