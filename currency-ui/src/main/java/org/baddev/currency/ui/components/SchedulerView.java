package org.baddev.currency.ui.components;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.exchange.entity.ExchangeOperation;
import org.baddev.currency.core.fetcher.NoRatesFoundException;
import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.scheduler.CronExchangeOperation;
import org.baddev.currency.ui.DoubleAmountToStringConverter;
import org.baddev.currency.ui.MyUI;
import org.baddev.currency.ui.components.base.AbstractCcyGridView;
import org.baddev.currency.ui.validation.CronValidator;
import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.baddev.currency.core.exchange.entity.ExchangeOperation.*;
import static org.baddev.currency.scheduler.CronExchangeOperation.P_CRON;
import static org.baddev.currency.ui.MyUI.myUI;
import static org.baddev.currency.ui.validation.ViewValidation.isValid;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
@SpringView(name = SchedulerView.NAME)
public class SchedulerView extends AbstractCcyGridView<CronExchangeOperation> {

    public static final String NAME = "scheduler";

    private static final String P_CNL_BTN = "cancellation";
    private static final String P_EXEC_BTN = "execution";

    private TextField amountF = new TextField("Amount:");
    private ComboBox fromCcyChoiseF = new ComboBox("From:");
    private ComboBox toCcyChoiseF = new ComboBox("To:");
    private TextField cronF = new TextField("Cron Expression:");
    private Button scheduleBtn = new Button("Schedule");
    private Button resetBtn = new Button("Reset");

    @Override
    public void init() {
        super.init();
        setup(CronExchangeOperation.class, MyUI.myUI().exchangeManager().getScheduledTasks().keySet(), P_ID, P_EXC_AM);
        addGeneratedButton(P_CNL_BTN, "Cancel", e -> {
            boolean canceled = myUI().exchangeManager().cancel(((CronExchangeOperation) e.getItemId()).getId());
            log.debug("Exchange task {} canceled: {}", ((CronExchangeOperation) e.getItemId()).getId(), canceled);
            container().removeItem(e.getItemId());
        });
        addGeneratedButton(P_EXEC_BTN, "Execute", e -> myUI().exchangeManager().execute((ExchangeOperation) e.getItemId()));
        grid.getColumn(P_DATE).setHeaderCaption("Date Added");
        grid.getColumn(P_CRON).setHeaderCaption("Cron Expression");
        grid.getColumn(P_AM).setRenderer(new HtmlRenderer(), new DoubleAmountToStringConverter());
        grid.setColumnOrder(P_DATE, P_AM_CD, P_EXC_AM_CD, P_AM, P_CRON, P_EXEC_BTN, P_CNL_BTN);
        grid.sort(P_DATE, SortDirection.DESCENDING);
    }

    @Override
    protected void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Rates", FontAwesome.DOLLAR, (MenuBar.Command) selectedItem -> MyUI.myUI().getNavigator().navigateTo(RatesView.NAME));
        menuBar.addItem("Exchanges", FontAwesome.EXCHANGE,
                (MenuBar.Command) selectedItem -> MyUI.myUI().getNavigator().navigateTo(ExchangesView.NAME));
    }

    @Override
    protected void customizeTopBar(HorizontalLayout topBar) {
        Collection<BaseExchangeRate> rates;
        rates = myUI().rateDao().findLastRates();
        if (rates.isEmpty())
            try {
                rates = myUI().fetcher().fetchCurrent();
            } catch (NoRatesFoundException e) {
                Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
            }
        Container c = new BeanItemContainer<>(BaseExchangeRate.class, rates);

        Arrays.stream(new ComboBox[]{fromCcyChoiseF, toCcyChoiseF}).forEach(cb -> {
            cb.setIcon(FontAwesome.MONEY);
            cb.setNullSelectionAllowed(false);
            cb.setTextInputAllowed(false);
            cb.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            cb.setImmediate(true);
            cb.setRequired(true);
            cb.setRequiredError("Currency must be selected");
            cb.setItemCaptionPropertyId(BaseExchangeRate.P_CCY);
            cb.setContainerDataSource(c);
            cb.addValueChangeListener((Property.ValueChangeListener) event -> {
                doCbValChange(event, cb.equals(fromCcyChoiseF) ? toCcyChoiseF : fromCcyChoiseF);
            });
        });

        Arrays.stream(new TextField[]{amountF, cronF}).forEach(tf -> {
            tf.setNullRepresentation("");
            tf.setRequired(true);
            tf.setImmediate(true);
            tf.setRequiredError("Field must be set");
        });

        amountF.setInputPrompt("Enter amount...");
        amountF.setConverter(new StringToDoubleConverter());
        amountF.setConversionError("Only numbers allowed");
        amountF.addValidator(new DoubleRangeValidator("Allowed range is from 0 to 9.9 billions", 0d, 9999999999d));
        amountF.setMaxLength(10);
        amountF.addValueChangeListener((Property.ValueChangeListener) event -> {
            if (isValid(amountF)) {
                if (!toCcyChoiseF.isEnabled() && !fromCcyChoiseF.isEnabled()) {
                    activateCcyCbs();
                    resetBtn.setEnabled(true);
                } else if (cronF.isEnabled()) {
                    if (isValid(cronF))
                        activateSchBtn();
                    else {
                        deactivateSchBtn();
                        cronF.focus();
                    }
                } else if (isValid(toCcyChoiseF, fromCcyChoiseF)) {
                    cronF.setEnabled(true);
                    cronF.focus();
                }
            } else deactivateSchBtn();
        });
        amountF.focus();

        cronF.setInputPrompt("Enter expression...");
        cronF.addValidator(new CronValidator("Cron expression is invalid"));
        cronF.setMaxLength(20);
        cronF.addValueChangeListener((Property.ValueChangeListener) event -> {
            if (isValid(cronF))
                if (isValid(amountF))
                    activateSchBtn();
                else amountF.focus();
            else deactivateSchBtn();
        });
        cronF.setIcon(FontAwesome.CALENDAR_TIMES_O);

        scheduleBtn.setIcon(FontAwesome.PLUS_CIRCLE);
        scheduleBtn.addClickListener((Button.ClickListener) event -> {
            ExchangeOperation exc = ExchangeOperation.newBuilder()
                    .id(System.currentTimeMillis())
                    .from(((BaseExchangeRate) fromCcyChoiseF.getValue()).getCcyCode())
                    .to(((BaseExchangeRate) toCcyChoiseF.getValue()).getCcyCode())
                    .date(LocalDate.fromDateFields(new Date()))
                    .amount((double) amountF.getConvertedValue())
                    .build();
            myUI().exchangeManager().schedule(exc, cronF.getValue());
            refresh(myUI().exchangeManager().getScheduledTasks().keySet(), ExchangeOperation.P_DATE, SortDirection.DESCENDING);
            resetInputs();
            amountF.focus();
        });
        scheduleBtn.setImmediate(true);

        resetBtn.setIcon(FontAwesome.REMOVE);
        resetBtn.setStyleName(ValoTheme.BUTTON_DANGER);
        resetBtn.setImmediate(true);
        resetBtn.addClickListener((Button.ClickListener) event -> {
            resetInputs();
            amountF.focus();
        });

        Label space = new Label();
        topBar.addComponents(amountF, fromCcyChoiseF, toCcyChoiseF, cronF, space, scheduleBtn, resetBtn);
        topBar.setExpandRatio(space, 1.0f);
        topBar.setComponentAlignment(fromCcyChoiseF, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(toCcyChoiseF, Alignment.MIDDLE_LEFT);
        topBar.setComponentAlignment(amountF, Alignment.BOTTOM_LEFT);
        topBar.setComponentAlignment(scheduleBtn, Alignment.BOTTOM_RIGHT);
        topBar.setComponentAlignment(resetBtn, Alignment.BOTTOM_RIGHT);
        topBar.setImmediate(true);
        toggleEnabled(false, fromCcyChoiseF, toCcyChoiseF, cronF, resetBtn, scheduleBtn);
    }

    private void doCbValChange(Property.ValueChangeEvent event, ComboBox another) {
        if (event.getProperty().getValue() != null) {
            if (isValid(another, amountF))
                cronF.setEnabled(true);
            else if (isValid(another) && !isValid(amountF))
                amountF.focus();
            else another.focus();
        }
    }

    private void activateSchBtn() {
        scheduleBtn.setEnabled(true);
        scheduleBtn.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        scheduleBtn.focus();
    }

    private void deactivateSchBtn() {
        scheduleBtn.setEnabled(false);
        scheduleBtn.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
    }

    private void activateCcyCbs() {
        toggleEnabled(true, fromCcyChoiseF, toCcyChoiseF);
        fromCcyChoiseF.focus();
    }

    private void resetInputs() {
        amountF.clear();
        fromCcyChoiseF.clear();
        toCcyChoiseF.clear();
        cronF.clear();
        toggleEnabled(false, fromCcyChoiseF, toCcyChoiseF, cronF, scheduleBtn, resetBtn);
        scheduleBtn.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
    }

}
