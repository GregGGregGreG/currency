package org.baddev.currency.fetcher.iso4217.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by Ilya on 15.04.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseIsoCcyEntry {

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

}
