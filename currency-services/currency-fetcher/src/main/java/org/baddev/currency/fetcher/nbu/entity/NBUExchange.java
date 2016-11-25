package org.baddev.currency.fetcher.nbu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Ilya on 11.04.2016.
 */
@XmlRootElement(name = "exchange")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NBUExchange {

    @XmlElementRef
    private List<NBUExchangeRate> exchangeRates;
}
