package org.baddev.currency.fetcher.other.entity;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by IPotapchuk on 4/14/2016.
 */
@XmlRootElement(name = "ISO_4217")
@XmlAccessorType(XmlAccessType.FIELD)
public class IsoCcyHistEntries {

    @XmlElementWrapper(name="HstrcCcyTbl")
    @XmlElementRef
    private List<IsoCcyHistEntry> entries;

    public List<IsoCcyHistEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<IsoCcyHistEntry> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IsoCcyHistEntries{");
        sb.append("entries=").append(entries);
        sb.append('}');
        return sb.toString();
    }

}
