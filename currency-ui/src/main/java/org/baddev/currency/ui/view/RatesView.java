package org.baddev.currency.ui.view;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.DateRangeValidator;
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

    @Value("${min_date_nbu}")
    private String minDateVal;

    private interface FetchOption {
        String ALL = "All";
        String CUR_DT = "Current Date";
        String CUST_DT = "Date";
        String CURRENCY_DT = "Currency & Date";

        String[] VALUES = {
                ALL,
                CUR_DT,
                CUST_DT,
                CURRENCY_DT
        };
    }

    private Button fetchBtn = new Button("Fetch");
    private PopupDateField df = new PopupDateField("Select Date:");

    @Override
    public void init() {
        setup(BaseExchangeRate.class, MyUI.current().fetcher().fetchCurrent(), P_ID);
        grid.setColumnOrder(P_DATE, P_BASE_CD, P_CD, P_RATE);
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
                minDate.toDate(), new Date(), Resolution.DAY));
        df.addValueChangeListener(event -> {
            fetchBtn.setEnabled(true);
            fetchBtn.focus();
        });
        df.setImmediate(true);

        fetchBtn.setEnabled(false);
        fetchBtn.addClickListener(event -> {
            if (df.getValue() != null) {
                Collection<BaseExchangeRate> rates = null;
                try {
                    rates = MyUI.current().fetcher().fetchByDate(new LocalDate(df.getValue()));
                } catch (NBUExchangeRateFetcher.RatesFetchingError e) {
                    Notification.show(e.toString(), Notification.Type.ERROR_MESSAGE);
                }
                refresh(rates, P_DATE);
            } else {
                Notification.show("Select date first", Notification.Type.WARNING_MESSAGE);
            }
        });
        fetchBtn.setImmediate(true);

        ComboBox cb = new ComboBox("Fetch by:");
        cb.setValue(1);
        cb.setNullSelectionAllowed(false);
        cb.setTextInputAllowed(false);
        cb.setContainerDataSource(new IndexedContainer(Arrays.asList(FetchOption.VALUES)));
        cb.addValueChangeListener(event -> {
            String option = (String) event.getProperty().getValue();
            switch (option) {
                case FetchOption.ALL:
                    toggleVisibility(false, df, fetchBtn);
                    refresh(MyUI.current().rateDao().loadAll(), P_DATE);
                    break;
                case FetchOption.CUR_DT:
                    toggleVisibility(false, df, fetchBtn);
                    try {
                        refresh(MyUI.current().fetcher().fetchCurrent(), P_DATE);
                    } catch (NBUExchangeRateFetcher.RatesFetchingError e) {
                        Notification.show(e.toString(), Notification.Type.ERROR_MESSAGE);
                    }
                    break;
                case FetchOption.CUST_DT:
                    toggleVisibility(true, df, fetchBtn);
                    break;
                case FetchOption.CURRENCY_DT:
                    toggleVisibility(true, df, fetchBtn);
                    break;
            }
        });
        cb.setImmediate(true);

        topBar.addComponent(cb);
        topBar.addComponent(df);
        topBar.addComponent(fetchBtn);
        topBar.setComponentAlignment(cb, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(df, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(fetchBtn, Alignment.BOTTOM_LEFT);
        topBar.setImmediate(true);
        toggleVisibility(false, df, fetchBtn);
    }

}
