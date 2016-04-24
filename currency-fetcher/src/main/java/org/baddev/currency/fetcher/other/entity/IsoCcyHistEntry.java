package org.baddev.currency.fetcher.other.entity;

import org.baddev.currency.fetcher.other.Iso4217CcyService;

import javax.xml.bind.annotation.*;

/**
 * Created by IPotapchuk on 4/14/2016.
 */
@XmlRootElement(name = "HstrcCcyNtry")
@XmlAccessorType(XmlAccessType.FIELD)
public class IsoCcyHistEntry extends BaseIsoCcyEntry {

    @XmlElement(name = Iso4217CcyService.WTHDRWL_DT_PARAM)
    private String withdrawDate;

    public void setWithdrawDate(String withdrawDate) {
        this.withdrawDate = withdrawDate;
    }

    public String getWithdrawDate() {
        return withdrawDate;
    }

    @Override
    public String toString() {
        return "IsoCcyHistEntry{" +
                "withdrawDate='" + withdrawDate + '\'' +
                "} " + super.toString();
    }
}
