package org.baddev.currency.fetcher.nbu.entity;

import org.baddev.currency.core.adapter.LocalDateAdapter;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.joda.time.LocalDate;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@XmlRootElement(name = "currency")
@XmlAccessorType(XmlAccessType.FIELD)
public class NBUExchangeRate implements IExchangeRate {

    @XmlTransient
    public static final LocalDate HRIVNA_INTR_DATE = new LocalDate(1996, 9, 2);
    @XmlTransient
    public static final String USSR_RUBLE_CD = "RUR";

    @XmlTransient
    private Long id;
    @XmlTransient
    private String baseLiterCode;
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

    @Override
    public void setId(Long value) {
        this.id = value;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setBaseCcy(String value) {
        this.baseLiterCode = value;
    }

    @Override
    public String getBaseCcy() {
        return baseLiterCode;
    }

    @Override
    public void setCcy(String value) {
        this.literCode = value;
    }

    @Override
    public String getCcy() {
        return literCode;
    }

    @Override
    public void setExchangeDate(LocalDate value) {
        this.exchangeDate = value;
    }

    @Override
    public LocalDate getExchangeDate() {
        return exchangeDate;
    }

    @Override
    public void setRate(Double value) {
        this.rate = value;
    }

    @Override
    public Double getRate() {
        return rate;
    }

    public int getDigitCode() {
        return digitCode;
    }

    public String getName() {
        return name;
    }

    @Override
    public void from(IExchangeRate from) {
        setId(from.getId());
        setBaseCcy(from.getBaseCcy());
        setCcy(from.getCcy());
        setExchangeDate(from.getExchangeDate());
        setRate(from.getRate());
    }

    @Override
    public <E extends IExchangeRate> E into(E into) {
        into.from(this);
        return into;
    }

    private void setDfBaseCcyCd() {
        baseLiterCode = (exchangeDate.isAfter(HRIVNA_INTR_DATE)
                || exchangeDate.isEqual(HRIVNA_INTR_DATE)) ? "UAH" : USSR_RUBLE_CD;
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        setDfBaseCcyCd();
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
}
