package org.baddev.currency.fetcher.impl.nbu.entity;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by IPotapchuk on 4/14/2016.
 */
@XmlRootElement(name = "ISO_4217")
@XmlAccessorType(XmlAccessType.FIELD)
public class IsoCcyEntries {

    @XmlElementWrapper(name="CcyTbl")
    @XmlElementRef
    private List<IsoCcyEntry> entries;

    public List<IsoCcyEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<IsoCcyEntry> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IsoCcyEntries{");
        sb.append("entries=").append(entries);
        sb.append('}');
        return sb.toString();
    }
}
