package org.baddev.currency.fetcher.nbu;

import org.apache.cxf.jaxrs.client.WebClient;
import org.baddev.currency.core.exception.NoRatesFoundException;
import org.baddev.currency.fetcher.ExchangeRateFetcher;
import org.baddev.currency.fetcher.ExtendedExchangeRateDao;
import org.baddev.currency.fetcher.nbu.entity.NBUExchange;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@Service("NBUFetcher")
@Transactional(noRollbackFor = NBURateFetcher.NoRatesLocallyFoundException.class)
public class NBURateFetcher implements ExchangeRateFetcher {

    private static final Logger log = LoggerFactory.getLogger(NBURateFetcher.class);
    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

    @Value("${param_date_nbu}")
    private String dateParam;
    @Value("${param_currency_nbu}")
    private String currencyParam;

    @Resource(name = "NBUClient")
    private WebClient client;

    @Autowired
    private ExtendedExchangeRateDao rateDao;

    static final class NoRatesLocallyFoundException extends RuntimeException {
        //nothing to add
    }

    private List<ExchangeRate> searchLocally(LocalDate date) {
        List<ExchangeRate> localRates = rateDao.fetchByExchangeDate(date);
        if (!localRates.isEmpty()) {
            log.debug("{} rates found locally.", localRates.size());
            return localRates;
        }
        log.debug("No rates found locally. Going to fetch by date {}", date.toString());
        throw new NoRatesLocallyFoundException();
    }

    @Override
    public Collection<? extends IExchangeRate> fetchCurrent() {
        Collection<ExchangeRate> rates;
        try {
            rates = searchLocally(new LocalDate());
        } catch (NoRatesLocallyFoundException ex) {
            rates = convert(client
                    .accept(MediaType.TEXT_XML_TYPE)
                    .get(NBUExchange.class));
            rateDao.insert(rates);
        }
        return rates;
    }

    @Override
    public Collection<? extends IExchangeRate> fetchByDate(LocalDate date) {
        Collection<ExchangeRate> rates;
        try {
            rates = searchLocally(date);
        } catch (NoRatesLocallyFoundException ex) {
            rates = convert(client
                    .accept(MediaType.TEXT_XML_TYPE)
                    .query(dateParam, date.toString(fmt))
                    .get(NBUExchange.class));
            rateDao.insert(rates);
        }
        return rates;
    }

    @Override
    public IExchangeRate fetchByCurrencyAndDate(Currency currency, LocalDate date) {
        ExchangeRate rate;
        try {
            rate = filter(currency, searchLocally(date));
        } catch (NoRatesLocallyFoundException ex) {
            rate = convert(client
                    .accept(MediaType.TEXT_XML_TYPE)
                    .query(currencyParam, currency.getCurrencyCode())
                    .query(dateParam, date.toString(fmt))
                    .get(NBUExchange.class))
                    .iterator().next();
            rateDao.insert(rate);
        }
        return rate;
    }

    private ExchangeRate filter(Currency currency, Collection<ExchangeRate> rates) {
        return rates.stream()
                .filter(rate -> rate.getCcy().equals(currency.getCurrencyCode()))
                .findFirst()
                .orElseThrow(NoRatesLocallyFoundException::new);
    }

    private Collection<ExchangeRate> convert(NBUExchange exchange) throws NoRatesFoundException {
        if (exchange.getExchangeRates() == null) {
            log.debug("No rates found in fetched payload");
            throw new NoRatesFoundException("No rates found by specified date");
        }
        log.debug("Fetched and extracted [{}] records", exchange.getExchangeRates().size());
        return exchange.getExchangeRates().stream()
                .filter(r -> !StringUtils.isEmpty(r.getCcy().trim())) //ignoring records without ccy
                .map(r -> r.into(new ExchangeRate()))
                .collect(Collectors.toList());
    }

}