package org.baddev.currency.fetcher.iso4217;

import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.fetcher.iso4217.entity.BaseIsoCcyEntry;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyEntry;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyHistEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Ilya on 15.04.2016.
 */
@Service("Iso4217Service")
public class Iso4217CcyServiceImpl implements Iso4217CcyService {

    private static final Logger log = LoggerFactory.getLogger(Iso4217CcyServiceImpl.class);

    private List<IsoCcyEntry> isoCurCcyEntries;
    private List<IsoCcyHistEntry> isoHistCcyEntries;

    @Autowired
    public Iso4217CcyServiceImpl(@Qualifier("IsoCurCcys") List<IsoCcyEntry> isoCurCcyEntries,
                                 @Qualifier("IsoHistCcys") List<IsoCcyHistEntry> isoHistCcyEntries) {
        if (isoHistCcyEntries.isEmpty() || isoHistCcyEntries.isEmpty()) {
            String msg = "Currency info service is currently unavailable. Check web client config or try again later.";
            log.error(msg + " Details: [isoCurCcyEntries:{} entries], [isoHistCcyEntries:{} entries]'",
                    isoCurCcyEntries.size(), isoHistCcyEntries.size());
            throw new ServiceException(msg);
        }
        this.isoCurCcyEntries = isoCurCcyEntries;
        this.isoHistCcyEntries = isoHistCcyEntries;
    }

    @Override
    public List<String> findCcyParamValues(IsoEntityParam target,
                                           IsoEntityParam keyParam,
                                           String keyParamVal) {
        Set<String> vals = Stream.concat(
                isoCurCcyEntries.stream(), isoHistCcyEntries.stream()
        ).filter(entry -> getValue(keyParam, entry).equalsIgnoreCase(keyParamVal)
                || getValue(keyParam, entry).toLowerCase().contains(keyParamVal.toLowerCase()))
                .map(entry -> getValue(target, entry))
                .collect(Collectors.toSet());
        return new ArrayList<>(new TreeSet<>(vals));
    }

    private static String getValue(IsoEntityParam param, BaseIsoCcyEntry entry) {
        String value = null;
        switch (param) {
            case CTRY_NM:
                value = entry.getCountryName();
                break;
            case CCY_NM:
                value = entry.getCcyName();
                break;
            case CCY:
                value = entry.getCcyCode();
                break;
            case CCY_NBR:
                value = entry.getCcyNumber();
                break;
            case CCY_MNR_UNTS:
                if (entry instanceof IsoCcyEntry)
                    value = ((IsoCcyEntry) entry).getCcyMnrUnts();
                break;
            case WTHDRWL_DT:
                if (entry instanceof IsoCcyHistEntry)
                    value = ((IsoCcyHistEntry) entry).getWithdrawDate();
                break;
        }
        return value == null ? "" : value;
    }

}
