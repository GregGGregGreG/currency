package org.baddev.currency.fetcher.iso4217;

import lombok.RequiredArgsConstructor;
import org.baddev.currency.fetcher.iso4217.entity.BaseIsoCcyEntry;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyEntry;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyHistEntry;
import org.slf4j.Logger;
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
@RequiredArgsConstructor
public class Iso4217CcyServiceImpl implements Iso4217CcyService {

    private final Logger                log;
    private final List<IsoCcyEntry>     isoCurCcyEntries;
    private final List<IsoCcyHistEntry> isoHistCcyEntries;

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
