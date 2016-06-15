package org.baddev.currency.core.fetcher.entity;

import org.joda.time.LocalDate;

/**
 * Created by IPotapchuk on 4/4/2016.
 */
public class BaseExchangeRate implements ExchangeRate {

    private long      id;
    private String    baseCcyCode;
    private String    ccyCode;
    private LocalDate date;
    private double    rate;

    public static final String P_ID      = "id";
    public static final String P_BASE_CD = "baseCcyCode";
    public static final String P_CCY     = "ccyCode";
    public static final String P_DATE    = "date";
    public static final String P_RATE    = "rate";

    public BaseExchangeRate() {
    }

    private BaseExchangeRate(Builder builder) {
        id = builder.id;
        setBaseCcyCode(builder.baseCcyCode);
        setCcyCode(builder.ccyCode);
        setDate(builder.date);
        setRate(builder.rate);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getBaseCcyCode() {
        return baseCcyCode;
    }

    public void setBaseCcyCode(String baseCcyCode) {
        this.baseCcyCode = baseCcyCode;
    }

    public String getCcyCode() {
        return ccyCode;
    }

    public void setCcyCode(String ccyCode) {
        this.ccyCode = ccyCode;
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
        if (!baseCcyCode.equals(that.baseCcyCode)) return false;
        if (!ccyCode.equals(that.ccyCode)) return false;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + baseCcyCode.hashCode();
        result = 31 * result + ccyCode.hashCode();
        result = 31 * result + date.hashCode();
        temp = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseExchangeRate{");
        sb.append("id=").append(id);
        sb.append(", baseCcyCode='").append(baseCcyCode).append('\'');
        sb.append(", ccyCode='").append(ccyCode).append('\'');
        sb.append(", date=").append(date);
        sb.append(", rate=").append(rate);
        sb.append('}');
        return sb.toString();
    }

    public static final class Builder {
        private long      id;
        private String    baseCcyCode;
        private String    ccyCode;
        private LocalDate date;
        private double    rate;

        private Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder baseCurrencyCode(String val) {
            baseCcyCode = val;
            return this;
        }

        public Builder ccyCode(String val) {
            ccyCode = val;
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
