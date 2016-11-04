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
import org.baddev.currency.core.event.EventPublisher;
import org.baddev.currency.core.exception.RatesNotFoundException;
import org.baddev.currency.core.task.NotifiableExchangeTask;
import org.baddev.currency.core.util.RoleEnum;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.mail.listener.MailExchangeCompletionListener;
import org.baddev.currency.security.utils.SecurityUtils;
import org.baddev.currency.ui.component.base.VerticalSpacedLayout;
import org.baddev.currency.ui.component.toolbar.GridButtonToolbar;
import org.baddev.currency.ui.component.view.base.AbstractCcyGridView;
import org.baddev.currency.ui.converter.DoubleAmountToStringConverter;
import org.baddev.currency.ui.exception.WrappedUIException;
import org.baddev.currency.ui.util.Navigator;
import org.baddev.currency.ui.util.NotificationUtils;
import org.baddev.currency.ui.validation.CronValidator;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.security.DeclareRoles;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation.P_FROM_CCY;
import static org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation.P_TO_CCY;
import static org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask.*;
import static org.baddev.currency.ui.util.FormatUtils.bold;
import static org.baddev.currency.ui.util.FormatUtils.joinByComma;
import static org.baddev.currency.ui.util.UIUtils.isAllValid;
import static org.baddev.currency.ui.util.UIUtils.toggleEnabled;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
@SpringView(name = SchedulerView.NAME)
@DeclareRoles({RoleEnum.ADMIN, RoleEnum.USER})
public class SchedulerView extends AbstractCcyGridView<IExchangeTask> {

    public static final String NAME = "scheduler";

    private TextField amountF        = new TextField("Amount:");
    private ComboBox  fromCcyChoiseF = new ComboBox("From:");
    private ComboBox  toCcyChoiseF   = new ComboBox("To:");
    private TextField cronF          = new TextField("Cron Expression:");
    private Button    scheduleBtn    = new Button("Schedule");
    private Button    resetBtn       = new Button("Reset");

    private Grid.FooterRow footer = grid.prependFooterRow();

    @Autowired private ExchangeTaskService taskService;
    @Autowired private Iso4217CcyService   ccyService;
    @Autowired private ExchangeRateService fetcher;

    private static NotifiableExchangeTask createTask(IExchangeTask taskData, BeanFactory beanFactory){
        NotifiableExchangeTask task = beanFactory.getBean(NotifiableExchangeTask.class);
        task.setEventPublisher(beanFactory.getBean(EventPublisher.class));
        task.setTaskData(taskData);
        MailExchangeCompletionListener listener = beanFactory.getBean(MailExchangeCompletionListener.class);
        listener.setEmail(SecurityUtils.getUserDetails().getEmail());
        return task;
    }

    @Override
    protected void postInit(VerticalSpacedLayout rootLayout) {
        setup(IExchangeTask.class,
                taskService.findForUser(SecurityUtils.getIdentityUserPrincipal().getId()), P_ID, P_USER_ID);

        grid.setCellDescriptionGenerator(cell -> {
            Object propId = cell.getPropertyId();
            if (P_CRON.equals(propId))
                try {
                    return CronExpressionDescriptor.getDescription((String) cell.getValue(), Options.twentyFourHour());
                } catch (ParseException e) {
                    throw new WrappedUIException("Unable to parse cron expression", e);
                }
            else if (P_FROM_CCY.equals(propId) || (P_TO_CCY).equals(propId))
                return joinByComma(
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
                P_CRON
        );

        container().addItemSetChangeListener((Container.ItemSetChangeListener) event -> {
            footer.getCell(P_CRON).setHtml("<b>Total: " + grid.getContainerDataSource().size() + "</b>");
            footer.getCell(P_ACTIVE).setHtml("<b>Active: " + taskService.getActiveCount() + "</b>");
        });

        grid.sort(P_ID, SortDirection.DESCENDING);
    }

    @Override
    protected void postRefresh(Collection<? extends IExchangeTask> data) {
        addRowFilter(new FilterConfig().setPropId(P_ACTIVE).setKind(FilterKind.SELECT)
                .setSelectOptions(data.stream()
                        .map(et -> et.getActive().toString())
                        .collect(Collectors.toList())));
        addRowFilter(new FilterConfig()
                .setPropId(P_ADDED_DATETIME)
                .setKind(FilterKind.DATETIME)
                .setResolution(DateTimeResolution.SECOND)
                .setExactDateOrDateTime(true));
        addRowFilter(new FilterConfig()
                .setPropId(ExchangeTask.P_FROM_CCY)
                .setKind(FilterKind.SELECT)
                .setSelectOptions(data.stream().map(IExchangeTask::getFromCcy).collect(Collectors.toList())));
        addRowFilter(new FilterConfig()
                .setPropId(ExchangeTask.P_TO_CCY)
                .setKind(FilterKind.SELECT)
                .setSelectOptions(data.stream().map(IExchangeTask::getToCcy).collect(Collectors.toList())));
    }

    @Override
    public Collection<MenuBar.MenuItem> customizeMenuBar(MenuBar menuBar) {
        return Arrays.asList(
                menuBar.addItem("Rates", FontAwesome.DOLLAR, selectedItem -> Navigator.navigate(RatesView.NAME)),
                menuBar.addItem("Exchanges", FontAwesome.EXCHANGE, selectedItem -> Navigator.navigate(ExchangesView.NAME)));
    }

    @Override
    protected void customizeGridBar(HorizontalLayout gridBar) {
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
            IExchangeTask saved = taskService.createReturning(taskData);
            taskService.schedule(createTask(saved, beanFactory), new CronTrigger(taskData.getCron()));
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
        gridBar.addComponents(amountF, fromCcyChoiseF, toCcyChoiseF, cronF, space, scheduleBtn, resetBtn);
        gridBar.setExpandRatio(space, 1.0f);
        gridBar.setComponentAlignment(fromCcyChoiseF, Alignment.MIDDLE_LEFT);
        gridBar.setComponentAlignment(toCcyChoiseF, Alignment.MIDDLE_LEFT);
        gridBar.setComponentAlignment(amountF, Alignment.BOTTOM_LEFT);
        gridBar.setComponentAlignment(scheduleBtn, Alignment.BOTTOM_RIGHT);
        gridBar.setComponentAlignment(resetBtn, Alignment.BOTTOM_RIGHT);
        gridBar.setImmediate(true);
        toggleEnabled(false, fromCcyChoiseF, toCcyChoiseF, cronF, resetBtn, scheduleBtn);

        GridButtonToolbar toolbar = new GridButtonToolbar(grid, true);
        toolbar
                .withButton("Execute", selected -> {
                    IExchangeTask task = (IExchangeTask) selected.iterator().next();
                    taskService.execute(createTask(task, beanFactory));
                    log.debug("Task {} executed manually", task.getId());
                })
                .withButton(selected -> {
                    IExchangeTask taskData = selected.iterator().hasNext() ? (IExchangeTask) selected.iterator().next() : null;
                    return taskData == null ? "Schedule" : taskData.getActive() ? "Cancel" : "Schedule";
                }, selected -> {
                    IExchangeTask taskData = (IExchangeTask) selected.iterator().next();
                    if (!taskData.getActive()) {
                        taskService.schedule(createTask(taskData, beanFactory), new CronTrigger(taskData.getCron()));
                        log.debug("Exchange task {} has been scheduled", taskData.getId());
                    } else {
                        taskService.terminate(taskData.getId());
                        log.debug("Exchange task {} has been canceled", taskData.getId());
                    }
                    refresh(taskService.findForUser(SecurityUtils.getIdentityUserPrincipal().getId()),
                            P_ID, SortDirection.DESCENDING);
                })
                .withButtonStyled("Remove", selected -> {
                    IExchangeTask taskData = (IExchangeTask) selected.iterator().next();
                    ConfirmDialog.show(UI.getCurrent(),
                            "Removal Confirmation",
                            "Are you really sure you want to remove exchange task " + bold(taskData.getId()) + "?",
                            "Yes",
                            "Cancel",
                            dialog -> {
                                if (dialog.isConfirmed()) {
                                    taskService.delete(taskData.getId());
                                    refresh(taskService.findForUser(SecurityUtils.getIdentityUserPrincipal().getId()),
                                            P_ID, SortDirection.DESCENDING);
                                    log.debug("Exchange task {} was removed", taskData.getId());
                                    NotificationUtils.notifySuccess("Task Removal",
                                            "Exchange task " + taskData.getId() + " successfully removed");
                                }
                            }).setContentMode(ConfirmDialog.ContentMode.HTML);
                }, ValoTheme.BUTTON_DANGER);

        gridBar.addComponent(toolbar);
        gridBar.setComponentAlignment(toolbar, Alignment.BOTTOM_RIGHT);

        grid.addSelectionListener(event -> {
            scheduleBtn.setVisible(event.getSelected().isEmpty());
            resetBtn.setVisible(event.getSelected().isEmpty());
        });

        amountF.addFocusListener(event -> {
            grid.getSelectionModel().reset();
        });
    }

    @Override
    public String getNameCaption() {
        return "Scheduler";
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
