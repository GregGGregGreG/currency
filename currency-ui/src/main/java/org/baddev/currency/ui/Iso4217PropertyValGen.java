package org.baddev.currency.ui;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import org.baddev.currency.fetcher.other.Iso4217CcyService;

import java.util.List;

public class Iso4217PropertyValGen extends PropertyValueGenerator<String> {

    private final Iso4217CcyService.Parameter param;
    private final Iso4217CcyService.Parameter keyParam;
    private final Iso4217CcyService service;
    private static final String GEN_COL_NULL_REPR = "Unknown";

    public Iso4217PropertyValGen(Iso4217CcyService.Parameter param, Iso4217CcyService.Parameter keyParam,
                                 Iso4217CcyService service) {
        this.service = service;
        this.param = param;
        this.keyParam = keyParam;
    }

    @Override
    public String getValue(Item item, Object itemId, Object propertyId) {
        List<String> vals = service.findCcyParamValues(param, keyParam,
                item.getItemProperty(keyParam.fieldName()).getValue().toString());
        if (vals.isEmpty())
            return GEN_COL_NULL_REPR;
        StringBuilder sb = new StringBuilder();
        vals.forEach(s -> sb.append(s).append(", "));
        sb.delete(sb.toString().length() - 2, sb.toString().length() - 1);
        return sb.toString();
    }

    @Override
    public Container.Filter modifyFilter(Container.Filter filter) throws UnsupportedFilterException {
        if (filter instanceof SimpleStringFilter) {
            SimpleStringFilter f = (SimpleStringFilter) filter;
            String refParam = Iso4217CcyService.Parameter.CCY.fieldName();
            List<String> refParamValues = service.findCcyParamValues(
                    Iso4217CcyService.Parameter.CCY,
                    Iso4217CcyService.Parameter.find(f.getPropertyId().toString()),
                    f.getFilterString()
            );
            if (refParamValues.size() > 0)
                if (refParamValues.size() == 1)
                    return new SimpleStringFilter(refParam, refParamValues.get(0), true, false);
                else
                    return new Or(refParamValues.stream()
                            .map(r -> new SimpleStringFilter(refParam, r, true, false))
                            .toArray(SimpleStringFilter[]::new));
        }
        return filter;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}