package org.baddev.currency.fetcher.impl.nbu;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.xml.XMLSource;
import org.baddev.currency.fetcher.ExchangeRateFetcher;
import org.baddev.currency.fetcher.impl.nbu.entity.NBUExchangeRate;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@Service("NBU")
public class NBUExchangeRateFetcher implements ExchangeRateFetcher {

    private static final Logger            log = LoggerFactory.getLogger(NBUExchangeRateFetcher.class);
    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

    @Value("${param_date_nbu}")     private String dateParam;
    @Value("${param_currency_nbu}") private String currencyParam;

    @Autowired
    private WebClient client;

    @Override
    public Collection<NBUExchangeRate> fetchCurrent() {
        XMLSource source = client
                .accept(MediaType.TEXT_XML_TYPE)
                .get(XMLSource.class);
        return extract(source);
    }

    @Override
    public Collection<NBUExchangeRate> fetchByDate(LocalDate date) {
        XMLSource source = client
                .accept(MediaType.TEXT_XML_TYPE)
                .query(dateParam, date.toString(fmt))
                .get(XMLSource.class);
        return extract(source);
    }

    @Override
    public NBUExchangeRate fetchByCurrencyAndDate(Currency currency, LocalDate date) {
        XMLSource source = client
                .accept(MediaType.TEXT_XML_TYPE)
                .query(currencyParam, currency.getCurrencyCode())
                .query(dateParam, date.toString(fmt))
                .get(XMLSource.class);
        return ((List<NBUExchangeRate>)extract(source)).get(0);
    }

    private Collection<NBUExchangeRate> extract(XMLSource source){
        source.setBuffering();
        List<NBUExchangeRate> extracted = Arrays
                .asList(source.getNodes("/exchange/currency", NBUExchangeRate.class));
        log.info("Fetched and extracted [{}] records", extracted.size());
        return extracted;
    }

}
