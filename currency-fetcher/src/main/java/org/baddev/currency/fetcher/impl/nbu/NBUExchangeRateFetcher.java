package org.baddev.currency.fetcher.impl.nbu;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.xml.XMLSource;
import org.baddev.currency.core.fetcher.ExchangeRateFetcher;
import org.baddev.currency.core.fetcher.entity.ExchangeRate;
import org.baddev.currency.dao.fetcher.ExchangeRateDao;
import org.baddev.currency.fetcher.impl.nbu.entity.NBUExchangeRate;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@Service("NBU")
@Transactional(noRollbackFor = NBUExchangeRateFetcher.NoRatesLocallyFoundException.class)
public class NBUExchangeRateFetcher implements ExchangeRateFetcher {

    private static final Logger            log = LoggerFactory.getLogger(NBUExchangeRateFetcher.class);
    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

    @Value("${param_date_nbu}")     private String dateParam;
    @Value("${param_currency_nbu}") private String currencyParam;

    @Autowired
    private WebClient client;

    @Autowired
    private ExchangeRateDao rateDao;

    public static final class NoRatesLocallyFoundException extends RuntimeException{
        //nothing to add
    }

    private Collection<ExchangeRate> searchLocally(LocalDate date){
        Collection<ExchangeRate> localRates = rateDao.loadByDate(date);
        if(!localRates.isEmpty()) {
            log.info("{} rates found locally.", localRates.size());
            return localRates;
        }
        throw new NoRatesLocallyFoundException();
    }

    @Override
    public Collection<ExchangeRate> fetchCurrent() {
        try {
            return searchLocally(new LocalDate());
        } catch (NoRatesLocallyFoundException ex){
            Collection<ExchangeRate> fetched = extract(client
                    .accept(MediaType.TEXT_XML_TYPE)
                    .get(XMLSource.class));
            rateDao.saveAll(fetched);
            return fetched;
        }
    }

    @Override
    public Collection<ExchangeRate> fetchByDate(LocalDate date) {
        try {
            return searchLocally(date);
        } catch (NoRatesLocallyFoundException ex){
            Collection<ExchangeRate> fetched = extract(client
                    .accept(MediaType.TEXT_XML_TYPE)
                    .query(dateParam, date.toString(fmt))
                    .get(XMLSource.class));
            rateDao.saveAll(fetched);
            return fetched;
        }
    }

    @Override
    public ExchangeRate fetchByCurrencyAndDate(Currency currency, LocalDate date) {
        try {
            return filter(currency, searchLocally(date));
        } catch (NoRatesLocallyFoundException ex) {
            ExchangeRate fetched =  extract(client
                    .accept(MediaType.TEXT_XML_TYPE)
                    .query(currencyParam, currency.getCurrencyCode())
                    .query(dateParam, date.toString(fmt))
                    .get(XMLSource.class))
                    .iterator().next();
            rateDao.save(fetched);
            return fetched;
        }
    }

    private ExchangeRate filter(Currency currency, Collection<ExchangeRate> rates){
        return rates.stream()
                .filter(rate -> rate.getCurrencyCode().equals(currency.getCurrencyCode()))
                .findFirst()
                .orElseThrow(NoRatesLocallyFoundException::new);
    }

    private Collection<ExchangeRate> extract(XMLSource source){
//        source.setBuffering();
        List<ExchangeRate> extracted = Arrays
                .asList(source.getNodes("/exchange/currency", NBUExchangeRate.class));
        log.info("Fetched and extracted [{}] records", extracted.size());
        return extracted;
    }

}
