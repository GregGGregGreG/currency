package org.baddev.currency.fetcher.other;

import java.util.List;

/**
 * Created by Ilya on 15.04.2016.
 */
public interface Iso4217CcyService {

    String CTRY_NM_PARAM      = "CtryNm";
    String CCY_NM_PARAM       = "CcyNm";
    String CCY_PARAM          = "Ccy";
    String CCY_NBR_PARAM      = "CcyNbr";
    String CCY_MNR_UNTS_PARAM = "CcyMnrUnts";
    String WTHDRWL_DT_PARAM   = "WthdrwlDt";

    List<String> findCcyParamValues(IsoEntityParam target, IsoEntityParam keyParam, String keyParamVal);

    default List<String> findCcyNamesByCode(String ccyCode){
        return findCcyParamValues(IsoEntityParam.CCY_NM, IsoEntityParam.CCY, ccyCode);
    }

    default List<String> findCcyCountriesByCode(String ccyCode){
        return findCcyParamValues(IsoEntityParam.CTRY_NM, IsoEntityParam.CCY, ccyCode);
    }

}
