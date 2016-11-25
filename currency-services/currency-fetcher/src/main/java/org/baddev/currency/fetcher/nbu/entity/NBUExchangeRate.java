package org.baddev.currency.fetcher.nbu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.baddev.currency.core.adapter.LocalDateAdapter;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.joda.time.LocalDate;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private String baseCcy;
    @XmlElement(name = "cc")
    private String ccy;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    @XmlElement(name = "exchangedate")
    private LocalDate exchangeDate;
    @XmlElement(name = "r030")
    private Integer digitCode;
    @XmlElement(name = "txt")
    private String name;
    @XmlElement(name = "rate")
    private Double rate;

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

    void afterUnmarshal(Unmarshaller u, Object parent) {
        baseCcy = (exchangeDate.isAfter(HRIVNA_INTR_DATE)
                || exchangeDate.isEqual(HRIVNA_INTR_DATE)) ? "UAH" : USSR_RUBLE_CD;
    }
}
