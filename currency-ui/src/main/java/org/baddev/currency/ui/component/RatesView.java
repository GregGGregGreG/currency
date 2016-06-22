package org.baddev.currency.ui.component;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.baddev.currency.core.fetcher.NoRatesFoundException;
import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.fetcher.other.Iso4217CcyService;
import org.baddev.currency.ui.component.base.AbstractCcyGridView;
import org.baddev.currency.ui.converter.DoubleAmountToStringConverter;
import org.baddev.currency.ui.util.Iso4217PropertyValGen;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.baddev.currency.core.fetcher.entity.BaseExchangeRate.*;
import static org.baddev.currency.ui.MyUI.myUI;

/**
 * Created by IPotapchuk on 4/8/2016.
 */
@SpringView(name = RatesView.NAME)
public class RatesView extends AbstractCcyGridView<BaseExchangeRate> {

    public  static final String NAME           = "rates";
    private static final String P_GEN_CCY_NAME = Iso4217CcyService.Parameter.CCY_NM.fieldName();

    @Value("${min_date_nbu}")
    private String minDateVal;

    private ComboBox       fetchOptCb = new ComboBox("Fetch:");
    private PopupDateField df         = new PopupDateField("Select Date:");
    private Button         fetchBtn   = new Button("Fetch");
    private TextField      filter     = new TextField();

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

    @Override
    public void init() {
        super.init();
        Collection<BaseExchangeRate> data = fetchCurrentRates();
        setup(BaseExchangeRate.class, data, P_ID);

        container().addItemSetChangeListener((Container.ItemSetChangeListener) event -> {
            containerWrapper().addGeneratedProperty(P_GEN_CCY_NAME,
                    new Iso4217PropertyValGen(Iso4217CcyService.Parameter.CCY_NM, Iso4217CcyService.Parameter.CCY,
                            iso4217Service));
        });

        refresh(data, P_CCY, null);

        grid.getColumn(P_BASE_CD).setHeaderCaption("Base Currency Code");
        grid.getColumn(P_CCY).setHeaderCaption("Currency Code");
        grid.getColumn(P_GEN_CCY_NAME).setHeaderCaption("Currency Name");
        grid.getColumn(P_RATE).setConverter(new DoubleAmountToStringConverter());
        grid.getColumns().forEach(c -> {
            if (!c.getPropertyId().equals(P_DATE))
                c.setRenderer(new HtmlRenderer());
        });

        grid.setColumnOrder(P_DATE, P_BASE_CD, P_CCY, P_GEN_CCY_NAME, P_RATE);
    }

    private Collection<BaseExchangeRate> fetchCurrentRates() {
        Collection<BaseExchangeRate> data;
        try {
            data = myUI().fetcher().fetchCurrent();
        } catch (NoRatesFoundException e) {
            data = new ArrayList<>();
            Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
        }
        return data;
    }

    @Override
    protected void filter(String text) {
        super.filter(text);
        if (!text.isEmpty()) {
            //let wrapper modify our gen property filter
            containerWrapper().addContainerFilter(new SimpleStringFilter(P_GEN_CCY_NAME, text, true, false));
            Or mergedOrs = new Or(container().getContainerFilters().stream().toArray(Container.Filter[]::new));
            container().removeAllContainerFilters();
            container().addContainerFilter(mergedOrs);
        }
    }

    @Override
    protected void customizeTopBar(HorizontalLayout topBar) {
        df.setResolution(Resolution.DAY);
        df.setIcon(FontAwesome.CALENDAR);
        df.setTextFieldEnabled(false);
        LocalDate minDate = LocalDate.parse(minDateVal, DateTimeFormat.forPattern("dd.MM.YYYY"));
        Date today = new Date();
        df.setRangeStart(minDate.toDate());
        df.setRangeEnd(today);
        df.addValidator(new DateRangeValidator("Select date in range ["
                + minDate.toString() + "..." + LocalDate.fromDateFields(today) + "]",
                minDate.toDate(), today, Resolution.DAY));
        df.setRequired(true);
        df.setRequiredError("Date must be selected");
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
                            myUI().fetcher().fetchByDate(new LocalDate(df.getValue()));
                    refresh(rates, P_CCY, null);
                } catch (NoRatesFoundException e) {
                    refresh(new ArrayList<>(), null, null);
                    Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
                }
            } else {
                Notification.show("Select date first", Notification.Type.WARNING_MESSAGE);
            }
        });
        fetchBtn.setImmediate(true);

        fetchOptCb.setContainerDataSource(new IndexedContainer(Arrays.asList(FetchOption.VALUES)));
        fetchOptCb.setIcon(FontAwesome.DOWNLOAD);
        fetchOptCb.select(FetchOption.CUR_DT);
        fetchOptCb.setNullSelectionAllowed(false);
        fetchOptCb.setTextInputAllowed(false);
        fetchOptCb.addValueChangeListener(event -> {
            String option = (String) event.getProperty().getValue();
            switch (option) {
                case FetchOption.ALL:
                    toggleVisible(false, df, fetchBtn);
                    refresh(myUI().rateDao().loadAll(), P_DATE, SortDirection.DESCENDING);
                    break;
                case FetchOption.CUR_DT:
                    toggleVisible(false, df, fetchBtn);
                    refresh(fetchCurrentRates(), P_CCY, null);
                    break;
                case FetchOption.CUST_DT:
                    toggleVisible(true, df, fetchBtn);
                    break;
            }
        });
        fetchOptCb.setImmediate(true);

        filter.setInputPrompt("Type to filter...");
        filter.addTextChangeListener((FieldEvents.TextChangeListener) event -> {
            filter(event.getText());
        });

        Label space = new Label();

        topBar.addComponents(fetchOptCb, df, fetchBtn, space, filter);
        topBar.setExpandRatio(space, 1.0f);

        topBar.setComponentAlignment(fetchOptCb, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(df, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(fetchBtn, Alignment.BOTTOM_LEFT);
        topBar.setComponentAlignment(filter, Alignment.BOTTOM_RIGHT);
        topBar.setImmediate(true);
        toggleVisible(false, df, fetchBtn);
    }

    @Override
    protected void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Exchanges", FontAwesome.EXCHANGE,
                (MenuBar.Command) selectedItem -> myUI().getNavigator().navigateTo(ExchangesView.NAME));
    }

}