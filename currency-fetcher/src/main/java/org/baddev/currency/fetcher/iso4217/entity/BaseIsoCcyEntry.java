package org.baddev.currency.fetcher.iso4217.entity;

import org.baddev.currency.core.MutableIdentity;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by Ilya on 15.04.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseIsoCcyEntry implements MutableIdentity<Long> {

    @XmlTransient
    private Long id;
    @XmlElement(name = Iso4217CcyService.CTRY_NM_PARAM)
    private String countryName;
    @XmlElement(name = Iso4217CcyService.CCY_NM_PARAM)
    private String ccyName;
    @XmlElement(name = Iso4217CcyService.CCY_PARAM)
    private String ccyCode;
    @XmlElement(name = Iso4217CcyService.CCY_NBR_PARAM)
    private String ccyNumber;

    public BaseIsoCcyEntry() {
    }

    private BaseIsoCcyEntry(Builder builder) {
        setId(builder.id);
        setCountryName(builder.countryName);
        setCcyName(builder.ccyName);
        setCcyCode(builder.ccyCode);
        setCcyNumber(builder.ccyNumber);
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

    public String getCcyName() {
        return ccyName;
    }

    public void setCcyName(String ccyName) {
        this.ccyName = ccyName;
    }

    public String getCcyCode() {
        return ccyCode;
    }

    public void setCcyCode(String ccyCode) {
        this.ccyCode = ccyCode;
    }

    public String getCcyNumber() {
        return ccyNumber;
    }

    public void setCcyNumber(String ccyNumber) {
        this.ccyNumber = ccyNumber;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseIsoCcyEntry{");
        sb.append("id=").append(id);
        sb.append(", countryName='").append(countryName).append('\'');
        sb.append(", ccyName='").append(ccyName).append('\'');
        sb.append(", ccyCode='").append(ccyCode).append('\'');
        sb.append(", ccyNumber=").append(ccyNumber);
        sb.append('}');
        return sb.toString();
    }

    public static final class Builder {
        private Long id;
        private String countryName;
        private String ccyName;
        private String ccyCode;
        private String ccyNumber;

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder countryName(String val) {
            countryName = val;
            return this;
        }

        public Builder ccyName(String val) {
            ccyName = val;
            return this;
        }

        public Builder ccyCode(String val) {
            ccyCode = val;
            return this;
        }

        public Builder ccyNumber(String val) {
            ccyNumber = val;
            return this;
        }

        public BaseIsoCcyEntry build() {
            return new BaseIsoCcyEntry(this);
        }
    }
}
