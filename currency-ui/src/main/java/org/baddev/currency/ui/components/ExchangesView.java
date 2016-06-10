package org.baddev.currency.ui.components;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.exchange.entity.ExchangeOperation;
import org.baddev.currency.core.fetcher.NoRatesFoundException;
import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.core.fetcher.entity.ExchangeRate;
import org.baddev.currency.ui.MyUI;
import org.baddev.currency.ui.components.base.AbstractCcyGridView;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.baddev.currency.core.exchange.entity.ExchangeOperation.*;
import static org.baddev.currency.core.fetcher.entity.BaseExchangeRate.P_DATE;

/**
 * Created by IPotapchuk on 4/8/2016.
 */
@SpringView(name = ExchangesView.NAME)
public class ExchangesView extends AbstractCcyGridView<ExchangeOperation> {

    public static final String NAME = "exchanges";

    private TextField amountF = new TextField("Amount:");
    private ComboBox fromCcyChoiseF = new ComboBox("From:");
    private ComboBox toCcyChoiseF = new ComboBox("To:");
    private PopupDateField exchDateF = new PopupDateField("Rate's date:");
    private Button exchBtn = new Button("Exchange");
    private Button resetBtn = new Button("Reset");

    @Value("${min_date_nbu}")
    private String minDateVal;

    @Override
    public void init() {
        super.init();
        setup(ExchangeOperation.class, MyUI.myUI().exchangeDao().loadAll(), P_ID);
        grid.setColumnOrder(P_DATE, P_AM_CD, P_EXC_AM_CD, P_AM, P_EXC_AM);
        grid.sort(P_DATE, SortDirection.DESCENDING);
    }

    @Override
    protected void customizeTopBar(HorizontalLayout topBar) {
        exchDateF.setResolution(Resolution.DAY);
        exchDateF.setIcon(FontAwesome.CALENDAR);
        exchDateF.setTextFieldEnabled(false);
        LocalDate minDate = LocalDate.parse(minDateVal, DateTimeFormat.forPattern("dd.MM.YYYY"));
        Date today = new Date();
        exchDateF.setRangeStart(minDate.toDate());
        exchDateF.setRangeEnd(today);
        exchDateF.addValidator(new DateRangeValidator("Select date in range ["
                + minDate.toString() + "..." + LocalDate.fromDateFields(today) + "]",
                minDate.toDate(), today, Resolution.DAY));
        exchDateF.setImmediate(true);

        Arrays.stream(new ComboBox[]{fromCcyChoiseF, toCcyChoiseF}).forEach(cb -> {
            cb.setNullSelectionAllowed(false);
            cb.setTextInputAllowed(false);
            cb.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            cb.setImmediate(true);
            cb.setItemCaptionPropertyId(BaseExchangeRate.P_CCY);
            cb.setIcon(FontAwesome.MONEY);
        });

        exchDateF.addValueChangeListener((Property.ValueChangeListener) event -> {
            Collection<BaseExchangeRate> rates = new ArrayList<>();
            try {
                rates = MyUI.myUI().fetcher().fetchByDate(LocalDate.fromDateFields(exchDateF.getValue()));
            } catch (NoRatesFoundException e) {
                Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
            }
            Container c = new BeanItemContainer<>(BaseExchangeRate.class, rates);
            fromCcyChoiseF.setContainerDataSource(c);
            toCcyChoiseF.setContainerDataSource(c);
            toggleEnabled(false, fromCcyChoiseF, toCcyChoiseF);
            resetBtn.setEnabled(true);
            toggleVisibility(true, fromCcyChoiseF, toCcyChoiseF, amountF);
            amountF.focus();
        });

        fromCcyChoiseF.addValueChangeListener((Property.ValueChangeListener) event -> {
            doCbValChange(event, toCcyChoiseF);
        });

        toCcyChoiseF.addValueChangeListener((Property.ValueChangeListener) event -> {
            doCbValChange(event, fromCcyChoiseF);
        });

        amountF.setInputPrompt("Enter amount...");
        amountF.setConverter(new StringToDoubleConverter());
        amountF.setConversionError("Only Numbers allowed");
        amountF.addValidator(new DoubleRangeValidator("Allowed range is from 0 to 9.9 billions", 0d, 9999999999d));
        amountF.setMaxLength(10);
        amountF.setNullRepresentation("");
        amountF.setRequired(true);
        amountF.setRequiredError("Field must be set");
        amountF.setImmediate(true);
        amountF.addValueChangeListener((Property.ValueChangeListener) event -> {
            if (amountF.isValid())
                activateCcyCbs();
            else
                exchBtn.setEnabled(false);
        });

        resetBtn.setIcon(FontAwesome.REMOVE, "Reset");
        resetBtn.setStyleName(ValoTheme.BUTTON_DANGER);
        resetBtn.setEnabled(false);
        resetBtn.setImmediate(true);
        resetBtn.addClickListener((Button.ClickListener) event -> {
            resetInputs();
            amountF.focus();
        });

        exchBtn.setEnabled(false);
        exchBtn.setIcon(FontAwesome.PLUS_CIRCLE);
        exchBtn.addClickListener((Button.ClickListener) event -> {
            ExchangeOperation exc = ExchangeOperation.newBuilder()
                    .from(((BaseExchangeRate) fromCcyChoiseF.getValue()).getCcyCode())
                    .to(((BaseExchangeRate) toCcyChoiseF.getValue()).getCcyCode())
                    .date(LocalDate.fromDateFields(exchDateF.getValue()))
                    .amount((double) amountF.getConvertedValue())
                    .build();
            MyUI.myUI().exchanger().exchange(exc, ((Collection<ExchangeRate>) fromCcyChoiseF.getItemIds()));
            refresh(MyUI.myUI().exchangeDao().loadAll(), ExchangeOperation.P_DATE, SortDirection.DESCENDING);
            resetInputs();
            amountF.focus();
        });
        exchBtn.setImmediate(true);

        Label space = new Label();
        topBar.addComponents(exchDateF, amountF, fromCcyChoiseF, toCcyChoiseF, space, exchBtn, resetBtn);
        topBar.setExpandRatio(space, 1.0f);
        topBar.setComponentAlignment(exchDateF, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(fromCcyChoiseF, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(toCcyChoiseF, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(amountF, Alignment.BOTTOM_LEFT);
        topBar.setComponentAlignment(exchBtn, Alignment.BOTTOM_RIGHT);
        topBar.setComponentAlignment(resetBtn, Alignment.BOTTOM_RIGHT);
        topBar.setImmediate(true);
        toggleVisibility(false, fromCcyChoiseF, toCcyChoiseF, amountF);
    }

    private void doCbValChange(Property.ValueChangeEvent event, ComboBox another) {
        if (event.getProperty().getValue() != null) {
            if (!another.isEmpty() && !amountF.isEmpty())
                activateExchBtn();
            else
                another.focus();
        }
    }

    private void activateExchBtn() {
        exchBtn.setEnabled(true);
        exchBtn.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        exchBtn.focus();
    }

    private void activateCcyCbs() {
        toggleEnabled(true, fromCcyChoiseF, toCcyChoiseF);
        fromCcyChoiseF.focus();
    }

    private void resetInputs() {
        amountF.clear();
        fromCcyChoiseF.clear();
        toCcyChoiseF.clear();
        toggleEnabled(false, fromCcyChoiseF, toCcyChoiseF, exchBtn);
        exchBtn.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
    }

    @Override
    protected void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Rates", FontAwesome.DOLLAR, (MenuBar.Command) selectedItem -> MyUI.myUI().getNavigator().navigateTo(RatesView.NAME));
        menuBar.addItem("Scheduler", FontAwesome.GEARS, (MenuBar.Command) selectedItem -> MyUI.myUI().getNavigator().navigateTo(SchedulerView.NAME));
    }
}
