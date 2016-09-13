package org.baddev.currency.fetcher.other;

import java.util.Arrays;

import static org.baddev.currency.fetcher.other.Iso4217CcyService.*;

public enum IsoEntityParam {

    CTRY_NM(CTRY_NM_PARAM, "countryName"),
    CCY_NM(CCY_NM_PARAM, "ccyName"),
    CCY(CCY_PARAM, "ccy"),
    CCY_NBR(CCY_NBR_PARAM, "ccyNumber"),
    CCY_MNR_UNTS(CCY_MNR_UNTS_PARAM, "ccyMnrUnts"),
    WTHDRWL_DT(WTHDRWL_DT_PARAM, "withdrawDate");

    IsoEntityParam(String paramName, String fieldName) {
        this.paramName = paramName;
        this.fieldName = fieldName;
    }

    private String paramName, fieldName;

    public static IsoEntityParam find(String fieldName) {
        return Arrays.stream(values()).filter(v -> v.fieldName().equals(fieldName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String paramName() {
        return paramName;
    }

    public String fieldName() {
        return fieldName;
    }
}