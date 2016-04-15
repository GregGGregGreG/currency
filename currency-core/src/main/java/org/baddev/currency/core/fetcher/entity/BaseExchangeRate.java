package org.baddev.currency.core.fetcher.entity;

import org.joda.time.LocalDate;

/**
 * Created by IPotapchuk on 4/4/2016.
 */
public class BaseExchangeRate implements ExchangeRate {

    private long id;
    private String baseCurrencyCode;
    private String ccy;
    private LocalDate date;
    private double rate;

    public static final String P_ID = "id";
    public static final String P_BASE_CD = "baseCurrencyCode";
    public static final String P_CCY = "ccy";
    public static final String P_DATE = "date";
    public static final String P_RATE = "rate";

    public BaseExchangeRate() {
    }

    private BaseExchangeRate(Builder builder) {
        id = builder.id;
        setBaseCurrencyCode(builder.baseCurrencyCode);
        setCcy(builder.currencyCode);
        setDate(builder.date);
        setRate(builder.rate);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseExchangeRate that = (BaseExchangeRate) o;

        if (id != that.id) return false;
        if (Double.compare(that.rate, rate) != 0) return false;
        if (!baseCurrencyCode.equals(that.baseCurrencyCode)) return false;
        if (!ccy.equals(that.ccy)) return false;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + baseCurrencyCode.hashCode();
        result = 31 * result + ccy.hashCode();
        result = 31 * result + date.hashCode();
        temp = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseExchangeRate{");
        sb.append("id=").append(id);
        sb.append(", baseCurrencyCode='").append(baseCurrencyCode).append('\'');
        sb.append(", ccy='").append(ccy).append('\'');
        sb.append(", date=").append(date);
        sb.append(", rate=").append(rate);
        sb.append('}');
        return sb.toString();
    }

    public static final class Builder {
        private long id;
        private String baseCurrencyCode;
        private String currencyCode;
        private LocalDate date;
        private double rate;

        private Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder baseCurrencyCode(String val) {
            baseCurrencyCode = val;
            return this;
        }

        public Builder currencyCode(String val) {
            currencyCode = val;
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
