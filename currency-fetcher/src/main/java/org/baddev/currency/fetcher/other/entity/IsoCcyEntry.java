package org.baddev.currency.fetcher.other.entity;

import org.baddev.currency.fetcher.other.Iso4217CcyService;

import javax.xml.bind.annotation.*;

/**
 * Created by IPotapchuk on 4/14/2016.
 */
@XmlRootElement(name = "CcyNtry")
@XmlAccessorType(XmlAccessType.FIELD)
public class IsoCcyEntry extends BaseIsoCcyEntry {

    @XmlElement(name = Iso4217CcyService.CCY_MNR_UNTS_PARAM)
    private String ccyMnrUnts;

    public String getCcyMnrUnts() {
        return ccyMnrUnts;
    }

    public void setCcyMnrUnts(String ccyMnrUnts) {
        this.ccyMnrUnts = ccyMnrUnts;
    }

    @Override
    public String toString() {
        return "IsoCcyEntry{" +
                "ccyMnrUnts='" + ccyMnrUnts + '\'' +
                "} " + super.toString();
    }
}
