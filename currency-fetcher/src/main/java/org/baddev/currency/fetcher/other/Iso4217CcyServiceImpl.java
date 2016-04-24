package org.baddev.currency.fetcher.other;

import org.baddev.currency.core.ServiceException;
import org.baddev.currency.fetcher.other.entity.BaseIsoCcyEntry;
import org.baddev.currency.fetcher.other.entity.IsoCcyEntry;
import org.baddev.currency.fetcher.other.entity.IsoCcyHistEntry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Ilya on 15.04.2016.
 */
@Service("Iso4217Service")
public class Iso4217CcyServiceImpl implements Iso4217CcyService {

    @Resource(name = "IsoCurCcys")
    private List<IsoCcyEntry> isoCurCcyEntries;

    @Resource(name = "IsoHistCcys")
    private List<IsoCcyHistEntry> isoHistCcyEntries;

    @Override
    public String findCcyParameter(Parameter param,
                                   Parameter keyParam,
                                   String keyParamVal) {
        if(isoHistCcyEntries.isEmpty() || isoHistCcyEntries.isEmpty())
            throw new ServiceException("Currency info service is currently unavailable. Try again later.");
        StringBuilder sb = new StringBuilder("");
        List<String> foundParams = Stream.concat(
                isoCurCcyEntries.stream(), isoHistCcyEntries.stream()
        ).filter(entry -> getValue(keyParam, entry).equals(keyParamVal))
                .map(entry -> getValue(param, entry))
                .collect(Collectors.toList());
        if (foundParams != null && foundParams.size() > 1) {
            foundParams.forEach(s -> {
                if(!sb.toString().contains(s))
                    sb.append(s).append(" @ ");
            });
            return sb.toString().replace('@', '|').substring(0, sb.toString().length() - 2);
        }
        return (foundParams != null && !foundParams.isEmpty()) ? foundParams.get(0) : null;
    }

    private static String getValue(Parameter param, BaseIsoCcyEntry entry) {
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
                else throwError(param, entry);
                break;
            case WTHDRWL_DT:
                if (entry instanceof IsoCcyHistEntry)
                    value = ((IsoCcyHistEntry) entry).getWithdrawDate();
                else throwError(param, entry);
                break;
            default:
                throwError(param, entry);
                break;
        }
        return value == null ? "" : value;
    }

    private static String throwError(Parameter param, BaseIsoCcyEntry entry) {
        throw new IllegalArgumentException("Param [" + param.getParamName() + "] " +
                "can't be found for currency entry [" + entry.getCcyCode() + "]");
    }

}
