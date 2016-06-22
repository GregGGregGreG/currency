package org.baddev.currency.ui.component;

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
import net.redhogs.cronparser.CronExpressionDescriptor;
import net.redhogs.cronparser.Options;
import org.baddev.currency.core.exchange.entity.ExchangeOperation;
import org.baddev.currency.core.fetcher.NoRatesFoundException;
import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.fetcher.other.Iso4217CcyService;
import org.baddev.currency.scheduler.CronExchangeOperation;
import org.baddev.currency.ui.MyUI;
import org.baddev.currency.ui.component.base.AbstractCcyGridView;
import org.baddev.currency.ui.converter.DoubleAmountToStringConverter;
import org.baddev.currency.ui.util.FormatUtils;
import org.baddev.currency.ui.validation.CronValidator;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import static org.baddev.currency.core.exchange.entity.ExchangeOperation.*;
import static org.baddev.currency.scheduler.CronExchangeOperation.P_CRON;
import static org.baddev.currency.ui.MyUI.myUI;
import static org.baddev.currency.ui.validation.ViewValidation.isValid;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
@SpringView(name = SchedulerView.NAME)
public class SchedulerView extends AbstractCcyGridView<CronExchangeOperation> {

    @Autowired
    private SettingsWindow settings;

    public static final String NAME = "scheduler";

    private static final String P_STATUS = "status";
    private static final String P_EXEC_BTN = "execution";
    private static final String P_MNG_BTN = "managing";
    private static final String P_RMV_BTN = "removal";
    private static final String P_NOTIF = "notifications";

    private TextField amountF = new TextField("Amount:");
    private ComboBox fromCcyChoiseF = new ComboBox("From:");
    private ComboBox toCcyChoiseF = new ComboBox("To:");
    private TextField cronF = new TextField("Cron Expression:");
    private Button scheduleBtn = new Button("Schedule");
    private Button resetBtn = new Button("Reset");

    private Grid.FooterRow footer = grid.prependFooterRow();

    @Override
    public void init() {
        super.init();

        setup(CronExchangeOperation.class, MyUI.myUI().scheduler().getScheduledTasks().keySet(), P_ID, P_EXC_AM);

        //footer refresh
        container().addItemSetChangeListener((Container.ItemSetChangeListener) event -> {
            footer.getCell(P_RMV_BTN).setHtml("<b>Total: " + grid.getContainerDataSource().size() + "</b>");
            footer.getCell(P_STATUS).setHtml("<b>Active: " + myUI().scheduler().getActiveCount() + "</b>");
        });

        addGeneratedStringProperty(P_STATUS, true, itemId ->
                myUI().scheduler().getScheduledTasks().get(itemId).isCancelled()
                        ? "<b>Canceled</b>" : "<b>Active</b>");

        addGeneratedButton(P_EXEC_BTN, "Execute", e -> {
            myUI().scheduler().execute((ExchangeOperation) e.getItemId());
            log.debug("Task {} executed manually", ((ExchangeOperation) e.getItemId()).getId());
        });

        addGeneratedButton(P_MNG_BTN,
                itemId -> myUI().scheduler().getScheduledTasks().get(itemId).isCancelled()
                        ? "Run" : "Cancel",
                event -> {
                    CronExchangeOperation op = (CronExchangeOperation) event.getItemId();
                    ScheduledFuture task = myUI().scheduler().getScheduledTasks().get(op);
                    if (task.isCancelled()) {
                        myUI().scheduler().reschedule(op);
                        log.debug("Exchange task {} has been rescheduled", op.getId());
                    } else {
                        myUI().scheduler().cancel(op.getId(), false);
                        log.debug("Exchange task {} has been canceled", op.getId());
                    }
                    refresh(myUI().scheduler().getScheduledTasks().keySet(), P_ID, SortDirection.DESCENDING);
                }
        );

        addGeneratedButton(P_RMV_BTN, "Remove", e -> {
            myUI().scheduler().cancel(((CronExchangeOperation) e.getItemId()).getId(), true);
            grid.getContainerDataSource().removeItem(e.getItemId());
            log.debug("Exchange task {} was removed", ((CronExchangeOperation) e.getItemId()).getId());
        });

        //description for cron an ccy cells
        grid.setCellDescriptionGenerator(cell -> {
            Object propId = cell.getPropertyId();
            if (P_CRON.equals(propId))
                try {
                    return CronExpressionDescriptor.getDescription((String) cell.getValue(), Options.twentyFourHour());
                } catch (ParseException e) {
                    String msg = "Unable to parse cron expression";
                    log.error(msg, e);
                    Notification.show(msg, Notification.Type.ERROR_MESSAGE);
                }
            else if (P_AM_CD.equals(propId) || P_EXC_AM_CD.equals(propId))
                return FormatUtils.formatCcyNamesList(
                        iso4217Service.findCcyParamValues(Iso4217CcyService.Parameter.CCY_NM,
                                Iso4217CcyService.Parameter.CCY, (String) cell.getValue())
                );
            return "";
        });

        grid.getColumn(P_DATE).setHeaderCaption("Date Added");
        grid.getColumn(P_CRON).setHeaderCaption("Cron Expression");
        grid.getColumn(P_AM_CD).setHeaderCaption("From");
        grid.getColumn(P_EXC_AM_CD).setHeaderCaption("To");
        grid.getColumn(P_AM).setRenderer(new HtmlRenderer(), new DoubleAmountToStringConverter());
        grid.setColumnOrder(P_STATUS, P_DATE, P_AM_CD, P_EXC_AM_CD, P_AM, P_CRON, P_EXEC_BTN, P_MNG_BTN);
        grid.sort(P_ID, SortDirection.DESCENDING);
    }

    @Override
    protected void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Rates", FontAwesome.DOLLAR,
                selectedItem -> myUI().getNavigator().navigateTo(RatesView.NAME));
        menuBar.addItem("Exchanges", FontAwesome.EXCHANGE,
                selectedItem -> myUI().getNavigator().navigateTo(ExchangesView.NAME));
        menuBar.addItem("Settings...", FontAwesome.GEAR,
                selectedItem -> myUI().addWindow(settings));
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
            myUI().scheduler().schedule(exc, cronF.getValue());
            refresh(myUI().scheduler().getScheduledTasks().keySet(), ExchangeOperation.P_ID, SortDirection.DESCENDING);
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