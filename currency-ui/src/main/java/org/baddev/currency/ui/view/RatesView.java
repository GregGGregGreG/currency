package org.baddev.currency.ui.view;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.fetcher.impl.nbu.NBUExchangeRateFetcher;
import org.baddev.currency.ui.MyUI;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.baddev.currency.core.fetcher.entity.BaseExchangeRate.*;

/**
 * Created by IPotapchuk on 4/8/2016.
 */
@SpringView(name = RatesView.NAME)
public class RatesView extends GridView<BaseExchangeRate> {

    public static final String NAME = "rates";

    private static final String P_CCY_NAME = "name";

    @Value("${min_date_nbu}")
    private String minDateVal;

    private interface FetchOption {
        String ALL = "All";
        String CUR_DT = "Today";
        String CUST_DT = "Date";

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

    @Override
    public void init() {
        setup(BaseExchangeRate.class, MyUI.current().fetcher().fetchCurrent(), P_ID);
        container().addNestedContainerProperty(P_CCY_NAME);
        grid.setColumnOrder(P_DATE, P_BASE_CD, P_CD, P_CCY_NAME, P_RATE);
    }

    @Override
    protected void refresh(Collection<BaseExchangeRate> data) {
        super.refresh(data);
//        containerWrapper().addGeneratedProperty(P_CCY_NAME, new PropertyValueGenerator<String>() {
//            @Override
//            public String getValue(Item item, Object itemId, Object propertyId) {
//                return findCcyName(((BeanItem<BaseExchangeRate>)item).getBean().getCurrencyCode());
//            }
//            @Override
//            public Class<String> getType() {
//                return String.class;
//            }
//        });
        data.forEach(r -> container().getItem(r).getItemProperty(P_CCY_NAME).setValue(findCcyName(r.getCurrencyCode())));
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

        fetchBtn.setEnabled(false);
        fetchBtn.addClickListener(event -> {
            if (df.getValue() != null) {
                try {
                    Collection<BaseExchangeRate> rates =
                            MyUI.current().fetcher().fetchByDate(new LocalDate(df.getValue()));
                    refresh(rates);
                } catch (NBUExchangeRateFetcher.RatesFetchingError e) {
                    Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
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
                    try {
                        refresh(MyUI.current().fetcher().fetchCurrent());
                    } catch (NBUExchangeRateFetcher.RatesFetchingError e) {
                        Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
                    }
                    break;
                case FetchOption.CUST_DT:
                    toggleVisibility(true, df, fetchBtn);
                    break;
            }
        });
        fetchOptCb.setImmediate(true);

        filter.setInputPrompt("Type to filter...");
        filter.addTextChangeListener((FieldEvents.TextChangeListener) event -> {
            fetchOptCb.select(FetchOption.ALL);
            filter(event.getText());
        });

        topBar.addComponent(fetchOptCb);
        topBar.addComponent(df);
        topBar.addComponent(fetchBtn);
        topBar.addComponent(filter);
        topBar.setComponentAlignment(fetchOptCb, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(df, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(fetchBtn, Alignment.BOTTOM_LEFT);
        topBar.setComponentAlignment(filter, Alignment.BOTTOM_RIGHT);
        topBar.setImmediate(true);
        toggleVisibility(false, df, fetchBtn);
    }

}
