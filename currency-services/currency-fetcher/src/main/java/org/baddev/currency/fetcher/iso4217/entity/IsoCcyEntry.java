package org.baddev.currency.fetcher.iso4217.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IPotapchuk on 4/14/2016.
 */
@XmlRootElement(name = "CcyNtry")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class IsoCcyEntry extends BaseIsoCcyEntry {

    @XmlElement(name = Iso4217CcyService.CCY_MNR_UNTS_PARAM)
    private String ccyMnrUnts;
}
