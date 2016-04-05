package org.baddev.currency.core.fetcher.entity;

import org.joda.time.LocalDate;

/**
 * Created by IPotapchuk on 4/4/2016.
 */
public class BaseExchangeRate implements ExchangeRate {

    private long id;
    private String baseLiterCode;
    private String literCode;
    private LocalDate exchangeDate;
    private double rate;

    public BaseExchangeRate() {
    }

    private BaseExchangeRate(Builder builder) {
        id = builder.id;
        setBaseLiterCode(builder.baseLiterCode);
        setLiterCode(builder.literCode);
        setExchangeDate(builder.exchangeDate);
        setRate(builder.rate);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String getBaseLiterCode() {
        return baseLiterCode;
    }

    public void setBaseLiterCode(String baseLiterCode) {
        this.baseLiterCode = baseLiterCode;
    }

    @Override
    public String getLiterCode() {
        return literCode;
    }

    public void setLiterCode(String literCode) {
        this.literCode = literCode;
    }

    @Override
    public LocalDate getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(LocalDate exchangeDate) {
        this.exchangeDate = exchangeDate;
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
        if (!baseLiterCode.equals(that.baseLiterCode)) return false;
        if (!literCode.equals(that.literCode)) return false;
        return exchangeDate.equals(that.exchangeDate);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + baseLiterCode.hashCode();
        result = 31 * result + literCode.hashCode();
        result = 31 * result + exchangeDate.hashCode();
        temp = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseExchangeRate{");
        sb.append("id=").append(id);
        sb.append(", baseLiterCode='").append(baseLiterCode).append('\'');
        sb.append(", literCode='").append(literCode).append('\'');
        sb.append(", exchangeDate=").append(exchangeDate);
        sb.append(", rate=").append(rate);
        sb.append('}');
        return sb.toString();
    }

    public static final class Builder {
        private long id;
        private String baseLiterCode;
        private String literCode;
        private LocalDate exchangeDate;
        private double rate;

        private Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder baseLiterCode(String val) {
            baseLiterCode = val;
            return this;
        }

        public Builder literCode(String val) {
            literCode = val;
            return this;
        }

        public Builder exchangeDate(LocalDate val) {
            exchangeDate = val;
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
