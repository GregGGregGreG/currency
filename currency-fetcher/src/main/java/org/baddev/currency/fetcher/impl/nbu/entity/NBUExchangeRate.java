package org.baddev.currency.fetcher.impl.nbu.entity;

import org.baddev.currency.core.fetcher.entity.ExchangeRate;
import org.baddev.currency.fetcher.impl.nbu.entity.adapters.LocalDateAdapter;
import org.joda.time.LocalDate;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Currency;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@XmlRootElement(name = "currency")
@XmlAccessorType(XmlAccessType.FIELD)
public class NBUExchangeRate implements ExchangeRate {

    @XmlTransient
    private Long id;
    @XmlTransient
    private String baseLiterCode = Currency.getInstance("UAH").getCurrencyCode();
    @XmlElement(name = "cc")
    private String literCode;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    @XmlElement(name = "exchangedate")
    private LocalDate exchangeDate;
    @XmlElement(name = "r030")
    private int digitCode;
    @XmlElement(name = "txt")
    private String name;
    @XmlElement(name = "rate")
    private double rate;

    public NBUExchangeRate() {
    }

    private NBUExchangeRate(Builder builder) {
        setId(builder.id);
        setBaseLiterCode(builder.baseCurrencyLiterCode);
        setLiterCode(builder.literCode);
        setExchangeDate(builder.exchangeDate);
        setDigitCode(builder.digitCode);
        setName(builder.name);
        setRate(builder.rate);
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

    public String getBaseCurrencyCode() {
        return baseLiterCode;
    }

    public void setBaseLiterCode(String baseLiterCode) {
        this.baseLiterCode = baseLiterCode;
    }

    public String getCurrencyCode() {
        return literCode;
    }

    public void setLiterCode(String literCode) {
        this.literCode = literCode;
    }

    @Override
    public LocalDate getDate() {
        return exchangeDate;
    }

    public void setExchangeDate(LocalDate exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public int getDigitCode() {
        return digitCode;
    }

    public void setDigitCode(int digitCode) {
        this.digitCode = digitCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NBUExchangeRate that = (NBUExchangeRate) o;

        if (digitCode != that.digitCode) return false;
        if (Double.compare(that.rate, rate) != 0) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (!baseLiterCode.equals(that.baseLiterCode)) return false;
        if (!literCode.equals(that.literCode)) return false;
        if (!exchangeDate.equals(that.exchangeDate)) return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + baseLiterCode.hashCode();
        result = 31 * result + literCode.hashCode();
        result = 31 * result + exchangeDate.hashCode();
        result = 31 * result + digitCode;
        result = 31 * result + name.hashCode();
        temp = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NBUExchangeRate{");
        sb.append("id=").append(id);
        sb.append(", baseLiterCode='").append(baseLiterCode).append('\'');
        sb.append(", literCode='").append(literCode).append('\'');
        sb.append(", exchangeDate=").append(exchangeDate);
        sb.append(", digitCode=").append(digitCode);
        sb.append(", name='").append(name).append('\'');
        sb.append(", rate=").append(rate);
        sb.append('}');
        return sb.toString();
    }

    public static final class Builder {
        private Long id;
        private String baseCurrencyLiterCode;
        private String literCode;
        private LocalDate exchangeDate;
        private int digitCode;
        private String name;
        private double rate;

        private Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder baseCurrencyLiterCode(String val) {
            baseCurrencyLiterCode = val;
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

        public Builder digitCode(int val) {
            digitCode = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder rate(double val) {
            rate = val;
            return this;
        }

        public NBUExchangeRate build() {
            return new NBUExchangeRate(this);
        }
    }
}
