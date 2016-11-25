package org.baddev.currency.fetcher.iso4217.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by IPotapchuk on 4/14/2016.
 */
@XmlRootElement(name = "ISO_4217")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IsoCcyEntries {

    @XmlElementWrapper(name="CcyTbl")
    @XmlElementRef
    private List<IsoCcyEntry> entries;
}
