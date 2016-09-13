package org.baddev.currency.ui.component.view;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToBooleanConverter;
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
import org.baddev.currency.core.exception.NoRatesFoundException;
import org.baddev.currency.fetcher.RateFetcherService;
import org.baddev.currency.fetcher.impl.nbu.NBU;
import org.baddev.currency.fetcher.other.Iso4217CcyService;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.scheduler.ExchangeTaskScheduler;
import org.baddev.currency.scheduler.task.exchange.ExchangeTaskService;
import org.baddev.currency.security.RoleEnum;
import org.baddev.currency.security.user.IdentityUser;
import org.baddev.currency.ui.component.base.AbstractCcyGridView;
import org.baddev.currency.ui.converter.DoubleAmountToStringConverter;
import org.baddev.currency.ui.util.FormatUtils;
import org.baddev.currency.ui.validation.CronValidator;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.DeclareRoles;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;

import static org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask.*;
import static org.baddev.currency.security.SecurityUtils.getUserDetails;
import static org.baddev.currency.ui.validation.ViewValidationHelper.isAllValid;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
@SpringView(name = SchedulerView.NAME)
@DeclareRoles({RoleEnum.ADMIN, RoleEnum.USER})
public class SchedulerView extends AbstractCcyGridView<ExchangeTask> {

    public static final String NAME = "scheduler";

    private static final String P_GEN_EXEC_BTN = "execution";
    private static final String P_GEN_MNG_BTN = "managing";
    private static final String P_GEN_RMV_BTN = "removal";

    private TextField amountF = new TextField("Amount:");
    private ComboBox fromCcyChoiseF = new ComboBox("From:");
    private ComboBox toCcyChoiseF = new ComboBox("To:");
    private TextField cronF = new TextField("Cron Expression:");
    private Button scheduleBtn = new Button("Schedule");
    private Button resetBtn = new Button("Reset");

    private Grid.FooterRow footer = grid.prependFooterRow();

    @Autowired
    private ExchangeTaskService<ExchangeTask> exchanger;
    @Autowired
    private ExchangeTaskScheduler scheduler;
    @Autowired
    private Iso4217CcyService ccyService;
    @NBU
    private RateFetcherService<ExchangeRate> fetcher;

    @Override
    public void init() {
        super.init();

        setup(ExchangeTask.class,
                exchanger.findForUser(getUserDetails(IdentityUser.class).getId()),
                P_ID, P_USER_ID);

        container().addItemSetChangeListener((Container.ItemSetChangeListener) event -> {
            footer.getCell(P_GEN_RMV_BTN).setHtml("<b>Total: " + grid.getContainerDataSource().size() + "</b>");
            footer.getCell(P_ACTIVE).setHtml("<b>Active: " + scheduler.getActiveCount() + "</b>");
        });

        addGeneratedButton(P_GEN_EXEC_BTN, "Execute", e -> {
            scheduler.execute((ExchangeTask) e.getItemId());
            log.debug("Task {} executed manually", ((ExchangeTask) e.getItemId()).getId());
        });

        addGeneratedButton(P_GEN_MNG_BTN, itemId -> ((ExchangeTask) itemId).getActive() ? "Cancel" : "Schedule",
                event -> {
                    ExchangeTask taskData = (ExchangeTask) event.getItemId();
                    if (!taskData.getActive()) {
                        scheduler.reschedule(taskData);
                        log.debug("Exchange task {} has been rescheduled", taskData.getId());
                    } else {
                        scheduler.cancel(taskData.getId(), false);
                        log.debug("Exchange task {} has been canceled", taskData.getId());
                    }
                    refresh(exchanger.findForUser(getUserDetails(IdentityUser.class).getId()),
                            P_ID, SortDirection.DESCENDING);
                }
        );

        addGeneratedButton(P_GEN_RMV_BTN, "Remove", e -> {
            scheduler.cancel(((ExchangeTask) e.getItemId()).getId(), true);
            grid.getContainerDataSource().removeItem(e.getItemId());
            log.debug("Exchange task {} was removed", ((ExchangeTask) e.getItemId()).getId());
        });

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
            else if (P_FROM_CCY.equals(propId) || (P_TO_CCY).equals(propId))
                return FormatUtils.formatCcyParamValuesList(
                        ccyService.findCcyNamesByCode((String) cell.getValue())
                );
            return "";
        });

        grid.getColumn(P_ACTIVE)
                .setHeaderCaption("Status")
                .setConverter(new StringToBooleanConverter("<b>Active</b>", "<b>Canceled</b>"))
                .setRenderer(new HtmlRenderer());
        grid.getColumn(P_ADDED_DATETIME).setHeaderCaption("Added");
        grid.getColumn(P_CRON).setHeaderCaption("Cron Expression");
        grid.getColumn(P_FROM_CCY).setHeaderCaption("From");
        grid.getColumn(P_TO_CCY).setHeaderCaption("To");
        grid.getColumn(P_AMOUNT).setRenderer(new HtmlRenderer(), new DoubleAmountToStringConverter());
        grid.setColumnOrder(
                P_ACTIVE,
                P_ADDED_DATETIME,
                P_FROM_CCY,
                P_TO_CCY,
                P_AMOUNT,
                P_CRON,
                P_GEN_EXEC_BTN,
                P_GEN_MNG_BTN,
                P_GEN_RMV_BTN
        );
        grid.sort(P_ID, SortDirection.DESCENDING);
    }

    @Override
    protected void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Rates", FontAwesome.DOLLAR,
                selectedItem -> navigateTo(RatesView.NAME));
        menuBar.addItem("Exchanges", FontAwesome.EXCHANGE,
                selectedItem -> navigateTo(ExchangesView.NAME));
    }

    @Override
    protected void customizeTopBar(HorizontalLayout topBar) {
        Collection<ExchangeRate> rates;
        rates = fetcher.findLast();
        if (rates.isEmpty())
            try {
                rates = fetcher.fetchCurrent();
            } catch (NoRatesFoundException e) {
                Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
            }

        Container c = new BeanItemContainer<>(ExchangeRate.class, rates);

        Arrays.stream(new ComboBox[]{fromCcyChoiseF, toCcyChoiseF}).forEach(cb -> {
            cb.setIcon(FontAwesome.MONEY);
            cb.setNullSelectionAllowed(false);
            cb.setTextInputAllowed(false);
            cb.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            cb.setImmediate(true);
            cb.setRequired(true);
            cb.setRequiredError("Currency must be selected");
            cb.setItemCaptionPropertyId(ExchangeRate.P_CCY);
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
            if (isAllValid(amountF)) {
                if (!toCcyChoiseF.isEnabled() && !fromCcyChoiseF.isEnabled()) {
                    activateCcyCbs();
                    resetBtn.setEnabled(true);
                } else if (cronF.isEnabled()) {
                    if (isAllValid(cronF))
                        activateSchBtn();
                    else {
                        deactivateSchBtn();
                        cronF.focus();
                    }
                } else if (isAllValid(toCcyChoiseF, fromCcyChoiseF)) {
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
            if (isAllValid(cronF))
                if (isAllValid(amountF))
                    activateSchBtn();
                else amountF.focus();
            else deactivateSchBtn();
        });
        cronF.setIcon(FontAwesome.CALENDAR_TIMES_O);

        scheduleBtn.setIcon(FontAwesome.PLUS_CIRCLE);
        scheduleBtn.addClickListener((Button.ClickListener) event -> {
            ExchangeTask taskData = new ExchangeTask()
                    .setUserId(getUserDetails(IdentityUser.class).getId())
                    .setFromCcy(((ExchangeRate) fromCcyChoiseF.getValue()).getCcy())
                    .setToCcy(((ExchangeRate) toCcyChoiseF.getValue()).getCcy())
                    .setAddedDatetime(LocalDateTime.now())
                    .setAmount((double) amountF.getConvertedValue())
                    .setCron(cronF.getValue());
            scheduler.schedule(taskData);
            refresh(exchanger.findForUser(getUserDetails(IdentityUser.class).getId()),
                    P_ID, SortDirection.DESCENDING);
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
            if (isAllValid(another, amountF))
                cronF.setEnabled(true);
            else if (isAllValid(another) && !isAllValid(amountF))
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
