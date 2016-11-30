package org.baddev.currency.fetcher.nbu;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.jaxrs.client.WebClient;
import org.baddev.currency.core.api.ExchangeRateFetcher;
import org.baddev.currency.core.meta.Prod;
import org.baddev.currency.fetcher.ExtendedExchangeRateDao;
import org.baddev.currency.fetcher.nbu.entity.NBUExchange;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@Service("NBUFetcher")
@Prod
@RequiredArgsConstructor
@Transactional(noRollbackFor = NBURateFetcher.NoRatesLocallyFoundException.class)
public class NBURateFetcher implements ExchangeRateFetcher {

    private DateTimeFormatter fmt;

    @Value("${nbu.url_param.date}")
    private String dateParam;
    @Value("${nbu.url_param.currency}")
    private String currencyParam;
    @Value("${nbu.url_param.date.format}")
    private String dateFormat;

    private final Logger                    log;
    private final ObjectProvider<WebClient> webClientProvider;
    private final ExtendedExchangeRateDao   rateDao;

    @PostConstruct
    public void init(){
        fmt = DateTimeFormat.forPattern(dateFormat);
    }

    static final class NoRatesLocallyFoundException extends RuntimeException {
    }

    private List<ExchangeRate> searchLocally(LocalDate date) {
        Assert.notNull(date, "date must be not null");
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
            rates = convert(webClientProvider.getObject()
                    .accept(MediaType.TEXT_XML_TYPE)
                    .get(NBUExchange.class));
            if (!rates.isEmpty()) rateDao.insert(rates);
        }
        return rates;
    }

    @Override
    public Collection<? extends IExchangeRate> fetchByDate(LocalDate date) {
        Collection<ExchangeRate> rates;
        try {
            rates = searchLocally(date);
        } catch (NoRatesLocallyFoundException ex) {
            rates = convert(webClientProvider.getObject()
                    .accept(MediaType.TEXT_XML_TYPE)
                    .query(dateParam, date.toString(fmt))
                    .get(NBUExchange.class));
            if (!rates.isEmpty()) rateDao.insert(rates);
        }
        return rates;
    }

    @Override
    public Optional<? extends IExchangeRate> fetchByCurrencyAndDate(Currency currency, LocalDate date) {
        Assert.notNull(currency, "currency must be not null");
        try {
            ExchangeRate rate = filter(currency, searchLocally(date));
            return Optional.ofNullable(rate);
        } catch (NoRatesLocallyFoundException ex) {
            Optional<ExchangeRate> maybeFetched = convert(webClientProvider.getObject()
                    .accept(MediaType.TEXT_XML_TYPE)
                    .query(currencyParam, currency.getCurrencyCode())
                    .query(dateParam, date.toString(fmt))
                    .get(NBUExchange.class))
                    .stream()
                    .findAny();
            maybeFetched.ifPresent(rateDao::insert);
            return maybeFetched;
        }
    }

    private ExchangeRate filter(Currency currency, Collection<ExchangeRate> rates) {
        return rates.stream()
                .filter(rate -> rate.getCcy().equals(currency.getCurrencyCode()))
                .findFirst()
                .orElseThrow(NoRatesLocallyFoundException::new);
    }

    private Collection<ExchangeRate> convert(NBUExchange exchange) {
        if (exchange.getExchangeRates() == null) {
            log.warn("No rates found in fetched payload");
            return Collections.emptyList();
        }
        log.debug("Fetched and extracted [{}] records", exchange.getExchangeRates().size());
        return exchange.getExchangeRates()
                .stream()
                .filter(r -> !StringUtils.isEmpty(r.getCcy().trim())) //ignoring records without ccy
                .map(r -> r.into(new ExchangeRate()))
                .collect(Collectors.toList());
    }

}
