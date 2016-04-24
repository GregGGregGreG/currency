package org.baddev.currency.fetcher.other;

/**
 * Created by Ilya on 15.04.2016.
 */
public interface Iso4217CcyService {

    String CTRY_NM_PARAM = "CtryNm";
    String CCY_NM_PARAM = "CcyNm";
    String CCY_PARAM = "Ccy";
    String CCY_NBR_PARAM = "CcyNbr";
    String CCY_MNR_UNTS_PARAM = "CcyMnrUnts";
    String WTHDRWL_DT_PARAM = "WthdrwlDt";

    enum Parameter {
        CTRY_NM(CTRY_NM_PARAM, "countryName"),
        CCY_NM(CCY_NM_PARAM, "ccyName"),
        CCY(CCY_PARAM, "ccyCode"),
        CCY_NBR(CCY_NBR_PARAM, "ccyNumber"),
        CCY_MNR_UNTS(CCY_MNR_UNTS_PARAM, "ccyMnrUnts"),
        WTHDRWL_DT(WTHDRWL_DT_PARAM, "withdrawDate");

        Parameter(String paramName, String fieldName) {
            this.paramName = paramName;
            this.fieldName = fieldName;
        }

        private String paramName, fieldName;

        public String getParamName() {
            return paramName;
        }

        public String getFieldName() {
            return fieldName;
        }

    }

    String findCcyParameter(Parameter param, Parameter keyParam, String keyParamVal);

}
