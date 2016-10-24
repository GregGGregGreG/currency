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
import org.baddev.currency.core.api.ExchangeRateService;
import org.baddev.currency.core.api.ExchangeTaskService;
import org.baddev.currency.core.exception.RatesNotFoundException;
import org.baddev.currency.core.task.NotifiableExchangeTask;
import org.baddev.currency.core.util.RoleEnum;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.security.utils.SecurityUtils;
import org.baddev.currency.ui.component.base.VerticalSpacedLayout;
import org.baddev.currency.ui.component.view.base.AbstractCcyGridView;
import org.baddev.currency.ui.converter.DoubleAmountToStringConverter;
import org.baddev.currency.ui.exception.WrappedUIException;
import org.baddev.currency.ui.util.FormatUtils;
import org.baddev.currency.ui.util.Navigator;
import org.baddev.currency.ui.validation.CronValidator;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.security.DeclareRoles;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;

import static org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask.*;
import static org.baddev.currency.ui.util.UIUtils.isAllValid;
import static org.baddev.currency.ui.util.UIUtils.toggleEnabled;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
@SpringView(name = SchedulerView.NAME)
@DeclareRoles({RoleEnum.ADMIN, RoleEnum.USER})
public class SchedulerView extends AbstractCcyGridView<IExchangeTask> {

    public  static final String NAME           = "scheduler";

    private static final String P_GEN_EXEC_BTN = "execution";
    private static final String P_GEN_MNG_BTN  = "managing";
    private static final String P_GEN_RMV_BTN  = "removal";

    private TextField amountF        = new TextField("Amount:");
    private ComboBox  fromCcyChoiseF = new ComboBox("From:");
    private ComboBox  toCcyChoiseF   = new ComboBox("To:");
    private TextField cronF          = new TextField("Cron Expression:");
    private Button    scheduleBtn    = new Button("Schedule");
    private Button    resetBtn       = new Button("Reset");

    private Grid.FooterRow footer = grid.prependFooterRow();

    @Autowired
    private ExchangeTaskService taskService;
    @Autowired
    private Iso4217CcyService ccyService;
    @Autowired
    private ExchangeRateService fetcher;

    private NotifiableExchangeTask createTask(IExchangeTask taskData){
        NotifiableExchangeTask task = beanFactory.getBean(NotifiableExchangeTask.class);
        task.setTaskData(taskData);
        return task;
    }

    @Override
    protected void postInit(VerticalSpacedLayout rootLayout) {
        setup(IExchangeTask.class,
                taskService.findForUser(SecurityUtils.getIdentityUserPrincipal().getId()),
                P_ID, P_USER_ID);

        container().addItemSetChangeListener((Container.ItemSetChangeListener) event -> {
            footer.getCell(P_GEN_RMV_BTN).setHtml("<b>Total: " + grid.getContainerDataSource().size() + "</b>");
            footer.getCell(P_ACTIVE).setHtml("<b>Active: " +
                    taskService.getActiveCount() + "</b>");
        });

        addGeneratedButton(P_GEN_EXEC_BTN, "Execute", e -> {
            taskService.execute(createTask((ExchangeTask)e.getItemId()));
            log.debug("Task {} executed manually", ((ExchangeTask) e.getItemId()).getId());
        });

        addGeneratedButton(P_GEN_MNG_BTN, itemId -> ((ExchangeTask) itemId).getActive() ? "Cancel" : "Schedule",
                event -> {
                    ExchangeTask taskData = (ExchangeTask) event.getItemId();
                    if (!taskData.getActive()) {
                        taskService.schedule(createTask(taskData), new CronTrigger(taskData.getCron()));
                        log.debug("Exchange task {} has been scheduled", taskData.getId());
                    } else {
                        taskService.terminate(taskData.getId());
                        log.debug("Exchange task {} has been canceled", taskData.getId());
                    }
                    refresh(taskService.findForUser(SecurityUtils.getIdentityUserPrincipal().getId()),
                            P_ID, SortDirection.DESCENDING);
                }
        );

        addGeneratedButton(P_GEN_RMV_BTN, "Remove", e -> {
            taskService.delete(((ExchangeTask) e.getItemId()).getId());
            grid.getContainerDataSource().removeItem(e.getItemId());
            log.debug("Exchange task {} was removed", ((ExchangeTask) e.getItemId()).getId());
        });

        grid.setCellDescriptionGenerator(cell -> {
            Object propId = cell.getPropertyId();
            if (P_CRON.equals(propId))
                try {
                    return CronExpressionDescriptor.getDescription((String) cell.getValue(), Options.twentyFourHour());
                } catch (ParseException e) {
                    throw new WrappedUIException("Unable to parse cron expression", e);
                }
            else if (P_FROM_CCY.equals(propId) || (P_TO_CCY).equals(propId))
                return FormatUtils.joinByComma(
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
    public void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Rates", FontAwesome.DOLLAR, selectedItem -> Navigator.navigate(RatesView.NAME));
        menuBar.addItem("Exchanges", FontAwesome.EXCHANGE, selectedItem -> Navigator.navigate(ExchangesView.NAME));
    }

    @Override
    protected void customizeGridBar(HorizontalLayout topBar) {
        Collection<? extends IExchangeRate> rates;
        rates = fetcher.findLast();
        if (rates.isEmpty())
            try {
                rates = fetcher.fetchCurrent();
            } catch (RatesNotFoundException e) {
                Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
            }

        Container c = new BeanItemContainer<>(IExchangeRate.class, rates);

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
                    enableCcyCbs();
                    resetBtn.setEnabled(true);
                } else if (cronF.isEnabled()) {
                    if (isAllValid(cronF))
                        enableSchBtn();
                    else {
                        disableSchBtn();
                        cronF.focus();
                    }
                } else if (isAllValid(toCcyChoiseF, fromCcyChoiseF)) {
                    cronF.setEnabled(true);
                    cronF.focus();
                }
            } else disableSchBtn();
        });
        amountF.focus();

        cronF.setInputPrompt("Enter expression...");
        cronF.addValidator(new CronValidator("Cron expression is invalid"));
        cronF.setMaxLength(20);
        cronF.addValueChangeListener((Property.ValueChangeListener) event -> {
            if (isAllValid(cronF))
                if (isAllValid(amountF))
                    enableSchBtn();
                else amountF.focus();
            else disableSchBtn();
        });
        cronF.setIcon(FontAwesome.CALENDAR_TIMES_O);

        scheduleBtn.setIcon(FontAwesome.PLUS_CIRCLE);
        scheduleBtn.addClickListener((Button.ClickListener) event -> {
            ExchangeTask taskData = new ExchangeTask();
            taskData.setUserId(SecurityUtils.getIdentityUserPrincipal().getId());
            taskData.setFromCcy(((ExchangeRate) fromCcyChoiseF.getValue()).getCcy());
            taskData.setToCcy(((ExchangeRate) toCcyChoiseF.getValue()).getCcy());
            taskData.setAddedDatetime(LocalDateTime.now());
            taskData.setAmount((double) amountF.getConvertedValue());
            taskData.setCron(cronF.getValue());
            taskData.setActive(false);
            IExchangeTask saved = taskService.saveReturning(taskData);
            taskService.schedule(createTask(saved), new CronTrigger(taskData.getCron()));
            refresh(taskService.findForUser(SecurityUtils.getIdentityUserPrincipal().getId()),
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

    private void enableSchBtn() {
        scheduleBtn.setEnabled(true);
        scheduleBtn.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        scheduleBtn.focus();
    }

    private void disableSchBtn() {
        scheduleBtn.setEnabled(false);
        scheduleBtn.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
    }

    private void enableCcyCbs() {
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
