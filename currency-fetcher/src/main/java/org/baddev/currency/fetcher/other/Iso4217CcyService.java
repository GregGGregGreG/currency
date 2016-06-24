package org.baddev.currency.fetcher.other;

import java.util.Arrays;
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

    enum Parameter {
        CTRY_NM(CTRY_NM_PARAM, "countryName"),
        CCY_NM(CCY_NM_PARAM, "ccyName"),
        CCY(CCY_PARAM, "ccy"),
        CCY_NBR(CCY_NBR_PARAM, "ccyNumber"),
        CCY_MNR_UNTS(CCY_MNR_UNTS_PARAM, "ccyMnrUnts"),
        WTHDRWL_DT(WTHDRWL_DT_PARAM, "withdrawDate");

        Parameter(String paramName, String fieldName) {
            this.paramName = paramName;
            this.fieldName = fieldName;
        }

        private String paramName, fieldName;

        public static Parameter find(String fieldName){
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

    List<String> findCcyParamValues(Parameter target, Parameter keyParam, String keyParamVal);

}
