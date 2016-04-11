package org.baddev.currency.fetcher.impl.nbu.entity;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by Ilya on 11.04.2016.
 */
@XmlRootElement(name = "exchange")
@XmlAccessorType(XmlAccessType.FIELD)
public class NBUExchange {
    @XmlElementRef
    private List<NBUExchangeRate> exchangeRates;

    public List<NBUExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(List<NBUExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NBUExchange{");
        sb.append("exchangeRates=").append(exchangeRates);
        sb.append('}');
        return sb.toString();
    }
}
