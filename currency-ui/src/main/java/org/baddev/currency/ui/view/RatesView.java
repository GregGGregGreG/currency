package org.baddev.currency.ui.view;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.fetcher.NoRatesFoundException;
import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.fetcher.other.Iso4217CcyService;
import org.baddev.currency.ui.MyUI;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

import static org.baddev.currency.core.fetcher.entity.BaseExchangeRate.*;

/**
 * Created by IPotapchuk on 4/8/2016.
 */
@SpringView(name = RatesView.NAME)
public class RatesView extends AbstractCcyGridView<BaseExchangeRate> {

    public static final String NAME = "rates";

    private static final String P_GEN_CCY_NAME = "currencyName";
    private static final String P_GEN_CTRY_NAME = "countries";

    @Value("${min_date_nbu}")
    private String minDateVal;

    private interface FetchOption {
        String ALL = "All";
        String CUR_DT = "Current";
        String CUST_DT = "By Date";

        String[] VALUES = {
                ALL,
                CUR_DT,
                CUST_DT
        };
    }

    private ComboBox fetchOptCb = new ComboBox("Fetch by:");
    private PopupDateField df = new PopupDateField("Select Date:");
    private Button fetchBtn = new Button("Fetch");
    private TextField filter = new TextField();

    private class CcyPropertyValGen extends PropertyValueGenerator<String> {
        private final Iso4217CcyService.Parameter param;
        private final Iso4217CcyService.Parameter keyParam;
        private static final String NULL_REPR_FOR_GEN_COL = "Unknown";

        public CcyPropertyValGen(Iso4217CcyService.Parameter param, Iso4217CcyService.Parameter keyParam) {
            this.param = param;
            this.keyParam = keyParam;
        }

        @Override
        public String getValue(Item item, Object itemId, Object propertyId) {
            String val = iso4217Service().findCcyParameter(param, keyParam,
                    item.getItemProperty(keyParam.getFieldName()).getValue().toString());
            if(val==null)
                return NULL_REPR_FOR_GEN_COL;
            return val;
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    }

    @Override
    public void init() {
        Collection<BaseExchangeRate> data = fetchCurrentRates();
        setup(BaseExchangeRate.class, data, P_ID);

        container().addItemSetChangeListener((Container.ItemSetChangeListener) event -> {
            containerWrapper().addGeneratedProperty(P_GEN_CCY_NAME,
                    new CcyPropertyValGen(Iso4217CcyService.Parameter.CCY_NM, Iso4217CcyService.Parameter.CCY));
            containerWrapper().addGeneratedProperty(P_GEN_CTRY_NAME,
                    new CcyPropertyValGen(Iso4217CcyService.Parameter.CTRY_NM, Iso4217CcyService.Parameter.CCY));
        });

        refresh(data);

        grid.getColumn(P_RATE).setRenderer(new NumberRenderer(Locale.US));

        grid.setColumnOrder(P_DATE, P_BASE_CD, P_CCY, P_GEN_CCY_NAME, P_GEN_CTRY_NAME, P_RATE);
    }

    private Collection<BaseExchangeRate> fetchCurrentRates(){
        Collection<BaseExchangeRate> data;
        try {
            data = MyUI.current().fetcher().fetchCurrent();
        } catch (NoRatesFoundException e) {
            data = new ArrayList<>();
            Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
        }
        return data;
    }

    @Override
    protected void customizeTopBar(HorizontalLayout topBar) {
        df.setResolution(Resolution.DAY);
        df.setTextFieldEnabled(false);
        LocalDate minDate = LocalDate.parse(minDateVal, DateTimeFormat.forPattern("dd.MM.YYYY"));
        Date today = new Date();
        df.setRangeStart(minDate.toDate());
        df.setRangeEnd(today);
        df.addValidator(new DateRangeValidator("Select date in range ["
                + minDate.toString() + "..." + LocalDate.fromDateFields(today) + "]",
                minDate.toDate(), today, Resolution.DAY));
        df.addValueChangeListener(event -> {
            if (df.isValid()) {
                fetchBtn.setEnabled(true);
                fetchBtn.focus();
            } else {
                fetchBtn.setEnabled(false);
            }
        });
        df.setImmediate(true);

        grid.addSelectionListener(selectionEvent -> {
            Object selected = getSelectedRow();
            if (selected != null) {
                Notification n = new Notification("" +
                        grid.getContainerDataSource().getItem(selected)
                                .getItemProperty(P_GEN_CTRY_NAME).getValue(),
                        Notification.Type.HUMANIZED_MESSAGE);
                n.setStyleName(ValoTheme.NOTIFICATION_CLOSABLE);
                n.setDelayMsec(-1);
                n.show(UI.getCurrent().getPage());
            }
        });

        fetchBtn.setEnabled(false);
        fetchBtn.addClickListener(event -> {
            if (df.getValue() != null) {
                try {
                    Collection<BaseExchangeRate> rates =
                            MyUI.current().fetcher().fetchByDate(new LocalDate(df.getValue()));
                    refresh(rates);
                } catch (NoRatesFoundException e) {
                    refresh(new ArrayList<>());
                    Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
                }
            } else {
                Notification.show("Select date first", Notification.Type.WARNING_MESSAGE);
            }
        });
        fetchBtn.setImmediate(true);

        fetchOptCb.setContainerDataSource(new IndexedContainer(Arrays.asList(FetchOption.VALUES)));
        fetchOptCb.select(FetchOption.CUR_DT);
        fetchOptCb.setNullSelectionAllowed(false);
        fetchOptCb.setTextInputAllowed(false);
        fetchOptCb.addValueChangeListener(event -> {
            String option = (String) event.getProperty().getValue();
            switch (option) {
                case FetchOption.ALL:
                    toggleVisibility(false, df, fetchBtn);
                    refresh(MyUI.current().rateDao().loadAll());
                    break;
                case FetchOption.CUR_DT:
                    toggleVisibility(false, df, fetchBtn);
                    refresh(fetchCurrentRates());
                    break;
                case FetchOption.CUST_DT:
                    toggleVisibility(true, df, fetchBtn);
                    break;
            }
        });
        fetchOptCb.setImmediate(true);

        filter.setInputPrompt("Type to filter...");
        filter.addTextChangeListener((FieldEvents.TextChangeListener) event -> {
            filter(event.getText());
        });

        topBar.addComponent(fetchOptCb);
        topBar.addComponent(df);
        topBar.addComponent(fetchBtn);

        Label space = new Label();
        topBar.addComponent(space);
        topBar.setExpandRatio(space, 1.0f);

        topBar.addComponent(filter);

        topBar.setComponentAlignment(fetchOptCb, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(df, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(fetchBtn, Alignment.BOTTOM_LEFT);
        topBar.setComponentAlignment(filter, Alignment.BOTTOM_RIGHT);
        topBar.setImmediate(true);
        toggleVisibility(false, df, fetchBtn);
    }

}
