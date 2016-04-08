package org.baddev.currency.ui.view;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.ui.MyUI;
import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.Date;

import static org.baddev.currency.core.fetcher.entity.BaseExchangeRate.*;

/**
 * Created by IPotapchuk on 4/8/2016.
 */
@SpringView(name = RatesView.NAME)
public class RatesView extends GridView<BaseExchangeRate> {

    public static final String NAME = "rates";

    public interface FetchOption {
        String CUR_DT = "Current Date";
        String CUST_DT = "Date";
        String CURRENCY_DT = "Currency and Date";

        String[] VALUES = {
                CUR_DT,
                CUST_DT,
                CURRENCY_DT
        };
    }

    private Button fetchBtn = new Button("Fetch");
    private PopupDateField df = new PopupDateField("Select Date");

    @Override
    public void init() {
        setup(BaseExchangeRate.class, MyUI.current().fetcher().fetchCurrent(), P_ID);
        grid.setColumnOrder(P_DATE, P_BASE_CD, P_CD, P_RATE);
    }

    @Override
    protected void customizeTopBar(HorizontalLayout topBar) {
        df.setResolution(Resolution.DAY);
        df.setTextFieldEnabled(false);
        df.addValidator(new DateRangeValidator("Invalid date selected",
                new LocalDate(1996, 1, 6).toDate(), new Date(), Resolution.DAY));
        df.addValueChangeListener(event1 -> fetchBtn.focus());

        fetchBtn.addClickListener(event1 -> {
            if (df.getValue() != null)
                refresh(MyUI.current().fetcher().fetchByDate(new LocalDate(df.getValue())));
            else
                Notification.show("Select date first", Notification.Type.WARNING_MESSAGE);
        });

        ComboBox cb = new ComboBox("Fetch by:");
        cb.setNullSelectionAllowed(false);
        cb.setValue(0);
        cb.setContainerDataSource(new IndexedContainer(Arrays.asList(FetchOption.VALUES)));
        cb.addValueChangeListener(event -> {
            String option = (String) event.getProperty().getValue();
            if (option.equals(FetchOption.CUR_DT)) {
                toggleComponents(false, df, fetchBtn);
                refresh(MyUI.current().fetcher().fetchCurrent());
            } else if (option.equals(FetchOption.CUST_DT)) {
                toggleComponents(true, df, fetchBtn);
            } else if (option.equals(FetchOption.CURRENCY_DT)) {
                toggleComponents(true, df, fetchBtn);
            }
        });
        topBar.addComponent(cb);
        attachComponents(topBar, df);
        attachComponents(topBar, fetchBtn);
        toggleComponents(false, df, fetchBtn);
    }

    private void toggleComponents(boolean visible, Component...components) {
        Arrays.stream(components).forEach(c -> c.setVisible(visible));
    }

    private void attachComponents(AbstractOrderedLayout l, Component...cs) {
        Arrays.stream(cs).forEach(c -> {
            if(l.getComponentIndex(c)==-1)
                l.addComponent(c);
        });
    }
}
