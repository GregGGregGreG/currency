package org.baddev.currency.core.exchanger.entity;

import org.baddev.currency.core.Identity;
import org.baddev.currency.core.ServiceException;
import org.baddev.currency.core.fetcher.entity.ExchangeRate;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@XmlRootElement(name = "exchange")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeOperation implements Identity<Long> {

    @XmlAttribute
    private Long          id;
    private String        fromCcy;
    private double        amount;
    private String        toCcy;
    private double        exchangedAmount;
    private LocalDate     ratesDate;
    private LocalDateTime performDate;

    public static final String P_ID        = "id";
    public static final String P_AM_CD     = "fromCcy";
    public static final String P_AM        = "amount";
    public static final String P_EXC_AM_CD = "toCcy";
    public static final String P_EXC_AM    = "exchangedAmount";
    public static final String P_DATE      = "ratesDate";
    public static final String P_PERF_DT   = "performDate";

    public ExchangeOperation() {
    }

    private ExchangeOperation(Builder builder) {
        setId(builder.id);
        setFromCcy(builder.amountCurrencyCode);
        setAmount(builder.amount);
        setToCcy(builder.exchangedAmountCurrencyCode);
        setExchangedAmount(builder.exchangedAmount);
        setRatesDate(builder.ratesDate);
        performDate = builder.performDate;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getRatesDate() {
        return ratesDate;
    }

    public void setRatesDate(LocalDate ratesDate) {
        this.ratesDate = ratesDate;
    }

    public LocalDateTime getPerformDate() {
        return performDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFromCcy() {
        return fromCcy;
    }

    public void setFromCcy(String fromCcy) {
        this.fromCcy = fromCcy;
    }

    public String getToCcy() {
        return toCcy;
    }

    public void setToCcy(String toCcy) {
        this.toCcy = toCcy;
    }

    public double getExchangedAmount() {
        return exchangedAmount;
    }

    public void setExchangedAmount(double exchangedAmount) {
        this.exchangedAmount = exchangedAmount;
    }

    public double exchange(Collection<ExchangeRate> rates) {
        performDate = LocalDateTime.now();
        if (fromCcy.equals("UAH")) {
            double rate = findRate(rates, toCcy);
            exchangedAmount = rate * amount;
        } else {
            double fPairRate = findRate(rates, fromCcy);
            double inDefaultBase = fPairRate * amount;
            double sPairRate = findRate(rates, toCcy);
            exchangedAmount = inDefaultBase / sPairRate;
        }
        return exchangedAmount;
    }

    private static Double findRate(Collection<ExchangeRate> rates, String ccy) {
        return rates.stream()
                .filter(r -> r.getCcy().equals(ccy))
                .mapToDouble(ExchangeRate::getRate)
                .findFirst()
                .orElseThrow(ServiceException::new);
    }

    public static final class Builder {
        private Long          id;
        private String        amountCurrencyCode;
        private double        amount;
        private String        exchangedAmountCurrencyCode;
        private double        exchangedAmount;
        private LocalDate     ratesDate;
        private LocalDateTime performDate;

        private Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder from(String val) {
            amountCurrencyCode = val;
            return this;
        }

        public Builder amount(double val) {
            amount = val;
            return this;
        }

        public Builder to(String val) {
            exchangedAmountCurrencyCode = val;
            return this;
        }

        public Builder exchangedAmount(double val) {
            exchangedAmount = val;
            return this;
        }

        public Builder ratesDate(LocalDate val) {
            ratesDate = val;
            return this;
        }

        public Builder performDate(LocalDateTime val){
            performDate = val;
            return this;
        }

        public ExchangeOperation build() {
            return new ExchangeOperation(this);
        }
    }
}
