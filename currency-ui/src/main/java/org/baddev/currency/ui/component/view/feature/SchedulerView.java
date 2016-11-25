package org.baddev.currency.ui.component.view.feature;

import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ProgressBarRenderer;
import com.vaadin.ui.themes.ValoTheme;
import net.redhogs.cronparser.CronExpressionDescriptor;
import net.redhogs.cronparser.Options;
import org.baddev.currency.core.api.ExchangeTaskService;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.core.security.utils.SecurityUtils;
import org.baddev.currency.exchanger.task.NotifiableExchangeTask;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.ui.component.toolbar.GridButtonToolbar;
import org.baddev.currency.ui.component.view.base.AbstractGridView;
import org.baddev.currency.ui.component.window.form.FormWindow;
import org.baddev.currency.ui.converter.DateToLocalDateTimeConverter;
import org.baddev.currency.ui.converter.DoubleAmountToStringConverter;
import org.baddev.currency.ui.model.grid.DateTimeResolution;
import org.baddev.currency.ui.model.grid.FilterConfig;
import org.baddev.currency.ui.model.grid.FilterKind;
import org.baddev.currency.ui.util.ButtonFactory;
import org.baddev.currency.ui.util.FormatUtils;
import org.baddev.currency.ui.util.Navigator;
import org.baddev.currency.ui.util.NotificationUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.security.RolesAllowed;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation.P_FROM_CCY;
import static org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation.P_TO_CCY;
import static org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask.*;
import static org.baddev.currency.ui.util.FormatUtils.bold;
import static org.baddev.currency.ui.util.FormatUtils.joinByComma;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
@SpringView(name = SchedulerView.NAME)
@RolesAllowed({RoleEnum.ADMIN, RoleEnum.USER})
public class SchedulerView extends AbstractGridView<ProgressExchangeTask> {

    private static final long serialVersionUID = -5154398389155929098L;

    public static final String NAME = "scheduler";

    private final ExchangeTaskService                    taskService;
    private final Iso4217CcyService                      ccyService;
    private final ObjectProvider<NotifiableExchangeTask> exchangeTaskProvider;
    private final ObjectProvider<SchedulerCreationView>  schedulerCreationViewProvider;
    private final ObjectProvider<ExcelExportView>        exportViewProvider;

    public SchedulerView(ExchangeTaskService taskService,
                         Iso4217CcyService ccyService,
                         ObjectProvider<NotifiableExchangeTask> exchangeTaskProvider,
                         ObjectProvider<SchedulerCreationView> schedulerCreationViewProvider,
                         ObjectProvider<ExcelExportView> exportViewProvider) {
        super(ProgressExchangeTask.class, P_ID, P_USER_ID);
        this.taskService = taskService;
        this.ccyService = ccyService;
        this.exchangeTaskProvider = exchangeTaskProvider;
        this.schedulerCreationViewProvider = schedulerCreationViewProvider;
        this.exportViewProvider = exportViewProvider;
    }

    @Override
    protected final void setup(Grid grid) {
        grid.setCellDescriptionGenerator(cell -> {
            Object propId = cell.getPropertyId();
            if (P_CRON.equals(propId))
                try {
                    return CronExpressionDescriptor.getDescription((String) cell.getValue(), Options.twentyFourHour());
                } catch (ParseException e) {
                    throw new RuntimeException("Unable to parse cron expression", e);
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
        grid.getColumn(P_ADDED_DATETIME)
                .setHeaderCaption("Added")
                .setRenderer(FormatUtils.dateRenderer(true), new DateToLocalDateTimeConverter());
        grid.getColumn(P_CRON).setHeaderCaption("Cron Expression");
        grid.getColumn(P_FROM_CCY).setHeaderCaption("From");
        grid.getColumn(P_TO_CCY).setHeaderCaption("To");
        grid.getColumn(P_AMOUNT).setRenderer(new HtmlRenderer(), new DoubleAmountToStringConverter());
        grid.getColumn("progress").setRenderer(new ProgressBarRenderer());
        grid.setColumnOrder(
                P_ACTIVE,
                P_ADDED_DATETIME,
                P_FROM_CCY,
                P_TO_CCY,
                P_AMOUNT,
                P_CRON,
                "progress"
        );

        Grid.FooterRow footer = getGrid().prependFooterRow();

        container().addItemSetChangeListener(event -> {
            footer.getCell(P_CRON).setHtml("<b>Total: " + grid.getContainerDataSource().size() + "</b>");
            footer.getCell(P_ACTIVE).setHtml("<b>Active: " + taskService.getActiveCount() + "</b>");
        });

//        grid.getContainerDataSource().getItemIds()
//                .stream()
//                .filter(id -> (Boolean) container().getItem(id).getItemProperty(P_ACTIVE).getValue())
//                .forEach(id -> {
//                    CronSequenceGenerator generator = new CronSequenceGenerator(String.valueOf(container().getItem(id).getItemProperty(P_CRON).getValue()));
//                    Date now = new Date();
//                    Date next = generator.next(now);
//                    long duration = next.getTime() - now.getTime();
//                    taskService.getFuture((Long) container().getItem(id).getItemProperty(P_ID).getValue()).ifPresent(f -> {
//                        Timer timer = new Timer(true);
//                        timer.schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                UI.getCurrent().access(() -> {
//                                    grid.getContainerDataSource().getItem(id).getItemProperty("progress").setValue(
//                                            (f.getDelay(TimeUnit.MILLISECONDS) / (duration * 0.01)) * 0.01d);
//                                });
//                            }
//                        }, 50);
//                    });
//                });

        setSortOrder(P_ID, SortDirection.DESCENDING);
        setHidableColumns(true);
    }

    @Override
    protected Collection<? extends ProgressExchangeTask> getItems() {
        return taskService.findForUser(SecurityUtils.getIdentityUserPrincipal().getId())
                .stream()
                .map(et -> et.into(new ProgressExchangeTask()))
                .collect(Collectors.toList());
    }

    @Override
    protected void postRefresh(Collection<? extends ProgressExchangeTask> data) {
        addRowFilter(new FilterConfig()
                .setPropId(P_ACTIVE)
                .setKind(FilterKind.SELECT)
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
        GridButtonToolbar toolbar = new GridButtonToolbar(getGrid(), true)
                .withExportBtn(exportViewProvider.getObject(this), ValoTheme.BUTTON_FRIENDLY)
                .withFactoryBtn(ButtonFactory.Mode.CREATE, event -> {
                    SchedulerCreationView view = schedulerCreationViewProvider.getIfAvailable();
                    FormWindow.show(view);
                    view.addFormCommittedListener(formBean -> {
                        refresh(getItems(),
                                P_ID, SortDirection.DESCENDING);
                    });
                })
                .withActionBtn("Execute", FontAwesome.PLAY_CIRCLE, selected -> {
                    IExchangeTask taskData = (IExchangeTask) selected.iterator().next();
                    NotifiableExchangeTask task = exchangeTaskProvider.getObject();
                    task.setTaskData(taskData);
                    taskService.execute(task);
                    log.debug("Task {} executed manually", task.getId());
                })
                .withActionBtn(selected -> {
                    IExchangeTask taskData = selected.iterator().hasNext() ? (IExchangeTask) selected.iterator().next() : null;
                    return taskData == null ? "Schedule" : taskData.getActive() ? "Cancel" : "Schedule";
                }, FontAwesome.CALENDAR_TIMES_O, selected -> {
                    IExchangeTask taskData = (IExchangeTask) selected.iterator().next();
                    if (!taskData.getActive()) {
                        NotifiableExchangeTask task = exchangeTaskProvider.getObject();
                        task.setTaskData(taskData);
                        taskService.schedule(task, taskData.getCron());
                        log.debug("Exchange task {} has been scheduled", taskData.getId());
                    } else {
                        taskService.terminate(taskData.getId());
                        log.debug("Exchange task {} has been canceled", taskData.getId());
                    }
                    refresh(getItems(), P_ID, SortDirection.DESCENDING);
                })
                .withActionFactoryBtn(ButtonFactory.Mode.REMOVE, selected -> {
                    IExchangeTask taskData = (IExchangeTask) selected.iterator().next();
                    ConfirmDialog.show(UI.getCurrent(),
                            "Removal Confirmation",
                            "Are you really sure you want to remove exchange task " + bold(taskData.getId()) + "?",
                            "Yes",
                            "Cancel",
                            dialog -> {
                                if (dialog.isConfirmed()) {
                                    taskService.delete(taskData.getId());
                                    refresh(getItems(), P_ID, SortDirection.DESCENDING);
                                    log.debug("Exchange task {} was removed", taskData.getId());
                                    NotificationUtils.notifySuccess("Task Removal",
                                            "Exchange task " + taskData.getId() + " successfully removed");
                                }
                            }).setContentMode(ConfirmDialog.ContentMode.HTML);
                });

        gridBar.addComponent(toolbar);
        gridBar.setComponentAlignment(toolbar, Alignment.BOTTOM_RIGHT);
    }

    @Override
    public String getNameCaption() {
        return "Scheduler";
    }

}
