package org.baddev.currency.fetcher.impl.nbu.entity;

import org.baddev.currency.core.Identity;

import javax.xml.bind.annotation.*;

/**
 * Created by IPotapchuk on 4/14/2016.
 */
@XmlRootElement(name = "CcyNtry")
@XmlAccessorType(XmlAccessType.FIELD)
public class IsoCcyEntry implements Identity<Long> {
    @XmlTransient
    private Long id;
    @XmlElement(name = "CtryNm")
    private String countryName;
    @XmlElement(name = "CcyNm")
    private String currencyName;
    @XmlElement(name = "Ccy")
    private String ccy;
    @XmlElement(name = "CcyNbr")
    private Integer ccyNumber;
    @XmlElement(name = "CcyMnrUnts")
    private String ccyMnrUnts;

    public IsoCcyEntry() {
    }

    private IsoCcyEntry(Builder builder) {
        setId(builder.id);
        setCountryName(builder.countryName);
        setCurrencyName(builder.currencyName);
        setCcy(builder.ccy);
        setCcyNumber(builder.ccyNumber);
        setCcyMnrUnts(builder.ccyMnrUnts);
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

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public Integer getCcyNumber() {
        return ccyNumber;
    }

    public void setCcyNumber(Integer ccyNumber) {
        this.ccyNumber = ccyNumber;
    }

    public String getCcyMnrUnts() {
        return ccyMnrUnts;
    }

    public void setCcyMnrUnts(String ccyMnrUnts) {
        this.ccyMnrUnts = ccyMnrUnts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IsoCcyEntry{");
        sb.append("id=").append(id);
        sb.append(", countryName='").append(countryName).append('\'');
        sb.append(", currencyName='").append(currencyName).append('\'');
        sb.append(", ccy='").append(ccy).append('\'');
        sb.append(", ccyNumber=").append(ccyNumber);
        sb.append(", ccyMnrUnts='").append(ccyMnrUnts).append('\'');
        sb.append('}');
        return sb.toString();
    }


    public static final class Builder {
        private Long id;
        private String countryName;
        private String currencyName;
        private String ccy;
        private Integer ccyNumber;
        private String ccyMnrUnts;

        private Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder countryName(String val) {
            countryName = val;
            return this;
        }

        public Builder currencyName(String val) {
            currencyName = val;
            return this;
        }

        public Builder ccy(String val) {
            ccy = val;
            return this;
        }

        public Builder ccyNumber(Integer val) {
            ccyNumber = val;
            return this;
        }

        public Builder ccyMnrUnts(String val) {
            ccyMnrUnts = val;
            return this;
        }

        public IsoCcyEntry build() {
            return new IsoCcyEntry(this);
        }
    }
}
