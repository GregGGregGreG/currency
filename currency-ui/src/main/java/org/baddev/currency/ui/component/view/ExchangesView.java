package org.baddev.currency.ui.component.view;

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
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.exception.NoRatesFoundException;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.baddev.currency.security.RoleEnum;
import org.baddev.currency.ui.component.base.AbstractCcyGridView;
import org.baddev.currency.ui.converter.DateToLocalDateTimeConverter;
import org.baddev.currency.ui.converter.DoubleAmountToStringConverter;
import org.baddev.currency.ui.util.FormatUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.security.DeclareRoles;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation.*;
import static org.baddev.currency.ui.validation.ViewComponentValidation.isValid;

/**
 * Created by IPotapchuk on 4/8/2016.
 */
@SpringView(name = ExchangesView.NAME)
@DeclareRoles({RoleEnum.ADMIN, RoleEnum.USER})
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
        setup(ExchangeOperation.class, facade().findExchangeOperations(), P_ID);

        grid.setCellDescriptionGenerator(cell -> {
            if (cell.getPropertyId().equals(P_FROM_CCY)
                    || cell.getPropertyId().equals(P_TO_CCY))
                return FormatUtils.formatCcyParamValuesList(
                        facade().findCcyNamesByCode((String) cell.getValue())
                );
            return "";
        });

        grid.getColumn(P_TO_AMOUNT)
                .setRenderer(new HtmlRenderer(), new DoubleAmountToStringConverter());
        grid.getColumn(P_FROM_AMOUNT)
                .setRenderer(new HtmlRenderer(), new DoubleAmountToStringConverter());
        grid.getColumn(P_RATES_DATE).setHeaderCaption("Rate's Date");
        grid.getColumn(P_PERFORM_DATETIME)
                .setHeaderCaption("Date")
                .setRenderer(new DateRenderer(DateFormat.getDateTimeInstance()), new DateToLocalDateTimeConverter());
        grid.getColumn(P_FROM_CCY).setHeaderCaption("From");
        grid.getColumn(P_TO_CCY).setHeaderCaption("To");
        grid.setColumnOrder(
                P_PERFORM_DATETIME,
                P_RATES_DATE,
                P_FROM_CCY,
                P_TO_CCY,
                P_FROM_AMOUNT,
                P_TO_AMOUNT);
        grid.sort(P_PERFORM_DATETIME, SortDirection.DESCENDING);
    }

    @Override
    protected void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Rates", FontAwesome.DOLLAR, selectedItem -> navigateTo(RatesView.NAME));
        menuBar.addItem("Scheduler", FontAwesome.GEARS, selectedItem -> navigateTo(SchedulerView.NAME));
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
        exchDateF.setRequired(true);
        exchDateF.setRequiredError("Date must be selected");

        Arrays.stream(new ComboBox[]{fromCcyChoiseF, toCcyChoiseF}).forEach(cb -> {
            cb.setNullSelectionAllowed(false);
            cb.setTextInputAllowed(false);
            cb.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            cb.setImmediate(true);
            cb.setRequired(true);
            cb.setRequiredError("Currency must be selected");
            cb.setItemCaptionPropertyId(ExchangeRate.P_CCY);
            cb.setIcon(FontAwesome.MONEY);
            cb.addValueChangeListener((Property.ValueChangeListener) event -> {
                doCbValChange(event, cb.equals(fromCcyChoiseF) ? toCcyChoiseF : fromCcyChoiseF);
            });
        });

        exchDateF.addValueChangeListener((Property.ValueChangeListener) event -> {
            Collection<ExchangeRate> rates = new ArrayList<>();
            try {
                rates = facade().fetchRatesByDate(LocalDate.fromDateFields(exchDateF.getValue()));
            } catch (NoRatesFoundException e) {
                Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
            }
            Container c = new BeanItemContainer<>(ExchangeRate.class, rates);
            fromCcyChoiseF.setContainerDataSource(c);
            toCcyChoiseF.setContainerDataSource(c);
            toggleEnabled(false, fromCcyChoiseF, toCcyChoiseF);
            resetBtn.setEnabled(true);
            toggleVisible(true, fromCcyChoiseF, toCcyChoiseF, amountF);
            amountF.focus();
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
            if (isValid(amountF)) {
                if (!toCcyChoiseF.isEnabled() && !fromCcyChoiseF.isEnabled()) {
                    activateCcyCbs();
                    resetBtn.setEnabled(true);
                } else if (isValid(toCcyChoiseF, fromCcyChoiseF)) {
                    activateExchBtn();
                }
            } else deactivateExchBtn();
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
            ExchangeOperation exc = new ExchangeOperation()
                    .setFromCcy(((ExchangeRate) fromCcyChoiseF.getValue()).getCcy())
                    .setToCcy(((ExchangeRate) toCcyChoiseF.getValue()).getCcy())
                    .setRatesDate(LocalDate.fromDateFields(exchDateF.getValue()))
                    .setFromAmount((double) amountF.getConvertedValue());
            facade().performExchange(exc, (Collection<ExchangeRate>) fromCcyChoiseF.getItemIds());
            refresh(facade().findExchangeOperations(), P_PERFORM_DATETIME, SortDirection.DESCENDING);
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
        toggleVisible(false, fromCcyChoiseF, toCcyChoiseF, amountF);
    }

    private void doCbValChange(Property.ValueChangeEvent event, ComboBox another) {
        if (event.getProperty().getValue() != null) {
            if (isValid(another, amountF))
                activateExchBtn();
            else if (isValid(another) && !isValid(amountF))
                amountF.focus();
            else another.focus();
        }
    }

    private void activateExchBtn() {
        exchBtn.setEnabled(true);
        exchBtn.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        exchBtn.focus();
    }

    private void deactivateExchBtn() {
        exchBtn.setEnabled(false);
        exchBtn.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
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

}
