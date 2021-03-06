package org.baddev.currency.ui.generator;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import lombok.RequiredArgsConstructor;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.fetcher.iso4217.IsoEntityParam;
import org.baddev.currency.ui.core.util.FormatUtils;

import java.util.List;

@RequiredArgsConstructor
public class Iso4217PropertyValGen extends PropertyValueGenerator<String> {

    private static final long serialVersionUID = 6263289958954941787L;

    private final IsoEntityParam    param;
    private final IsoEntityParam    keyParam;
    private final Iso4217CcyService service;

    @Override
    public String getValue(Item item, Object itemId, Object propertyId) {
        return FormatUtils.joinByComma(
                service.findCcyParamValues(param, keyParam, item.getItemProperty(keyParam.fieldName()).getValue().toString())
        );
    }

    @Override
    public Container.Filter modifyFilter(Container.Filter filter) throws UnsupportedFilterException {
        if (filter instanceof SimpleStringFilter) {
            SimpleStringFilter f = (SimpleStringFilter) filter;
            String refParam = IsoEntityParam.CCY.fieldName();
            List<String> refParamValues = service.findCcyParamValues(
                    IsoEntityParam.CCY,
                    IsoEntityParam.find(f.getPropertyId().toString()),
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