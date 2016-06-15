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
    private final Iso4217CcyService           service;

    public Iso4217PropertyValGen(Iso4217CcyService.Parameter param, Iso4217CcyService.Parameter keyParam,
                                 Iso4217CcyService service) {
        this.service = service;
        this.param = param;
        this.keyParam = keyParam;
    }

    @Override
    public String getValue(Item item, Object itemId, Object propertyId) {
        return FormatUtils.formatCcyNamesList(
                service.findCcyParamValues(param, keyParam, item.getItemProperty(keyParam.fieldName()).getValue().toString())
        );
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