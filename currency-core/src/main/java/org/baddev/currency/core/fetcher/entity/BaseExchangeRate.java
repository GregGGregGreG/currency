package org.baddev.currency.core.fetcher.entity;

import org.joda.time.LocalDate;

import java.util.Objects;

/**
 * Created by IPotapchuk on 4/4/2016.
 */
public class BaseExchangeRate implements ExchangeRate {

    private long id;
    private String baseCcy;
    private String ccy;
    private LocalDate date;
    private double rate;

    public static final String P_ID = "id";
    public static final String P_BASE_CD = "baseCcy";
    public static final String P_CCY = "ccy";
    public static final String P_DATE = "date";
    public static final String P_RATE = "rate";

    public BaseExchangeRate() {
    }

    private BaseExchangeRate(Builder builder) {
        id = builder.id;
        setBaseCcy(builder.baseCcy);
        setCcy(builder.ccy);
        setDate(builder.date);
        setRate(builder.rate);
    }

    private static void checkCcy(String ccy) {
        Objects.requireNonNull(ccy);
        if (ccy.isEmpty() || ccy.length() != 3)
            throw new IllegalArgumentException("Currency code must consist of 3 characters");
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseCcy() {
        return baseCcy;
    }

    public void setBaseCcy(String baseCcy) {
        checkCcy(baseCcy);
        this.baseCcy = baseCcy;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        checkCcy(ccy);
        this.ccy = ccy;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public Double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public static final class Builder {
        private long id;
        private String baseCcy;
        private String ccy;
        private LocalDate date;
        private double rate;

        private Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder baseCcy(String val) {
            baseCcy = val;
            return this;
        }

        public Builder ccy(String val) {
            ccy = val;
            return this;
        }

        public Builder date(LocalDate val) {
            date = val;
            return this;
        }

        public Builder rate(double val) {
            rate = val;
            return this;
        }

        public BaseExchangeRate build() {
            return new BaseExchangeRate(this);
        }
    }
}
