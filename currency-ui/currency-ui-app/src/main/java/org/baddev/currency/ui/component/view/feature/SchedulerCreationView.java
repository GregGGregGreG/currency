package org.baddev.currency.ui.component.view.feature;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.api.ExchangeRateService;
import org.baddev.currency.core.api.ExchangeTaskService;
import org.baddev.currency.core.dto.SchedulerDTO;
import org.baddev.currency.core.exception.RatesNotFoundException;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.core.security.utils.SecurityUtils;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.ui.core.component.view.AbstractFormView;
import org.baddev.currency.ui.core.util.ButtonFactory;
import org.baddev.currency.ui.core.util.NotificationUtils;
import org.baddev.currency.ui.validation.CronValidator;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 11/22/2016.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RolesAllowed({RoleEnum.ADMIN, RoleEnum.USER})
public class SchedulerCreationView extends AbstractFormView<SchedulerDTO> {

    private final ExchangeRateService rateService;
    private final ExchangeTaskService taskService;

    public SchedulerCreationView(ExchangeRateService rateService,
                                 ExchangeTaskService taskService) {
        super(SchedulerDTO.class, ValoTheme.PANEL_BORDERLESS);
        this.rateService = rateService;
        this.taskService = taskService;
    }

    @Override
    public String getNameCaption() {
        return "Create New Scheduler";
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<SchedulerDTO> binder, Button submitBtn) {
        Collection<? extends IExchangeRate> rates;
        rates = rateService.findLast();
        if (rates.isEmpty())
            try {
                rates = rateService.fetchCurrent();
            } catch (RatesNotFoundException e) {
                NotificationUtils.notifyWarn("Error Obtaining Rates", "Failed to get rates");
            }

        Collection<String> ccys = rates.stream().map(IExchangeRate::getCcy).collect(Collectors.toList());

        ComboBox from = new ComboBox("Select Source Currency", ccys);
        ComboBox to = new ComboBox("Select Target Currency", ccys);

        Arrays.stream(new ComboBox[]{from, to}).forEach(cb -> {
            cb.setIcon(FontAwesome.MONEY);
            cb.setNullSelectionAllowed(false);
            cb.setTextInputAllowed(false);
            cb.setRequired(true);
        });

        TextField amount = binder.buildAndBind("Enter Amount", "amount", TextField.class);
        TextField cron = binder.buildAndBind("Enter Cron Expression", "cron", TextField.class);

        amount.setMaxLength(10);
        amount.setConverter(new StringToDoubleConverter());
        amount.setRequired(true);

        cron.setMaxLength(20);
        cron.setIcon(FontAwesome.CALENDAR_TIMES_O);
        cron.addValidator(new CronValidator("Cron expression is invalid"));
        cron.setRequired(true);

        configureFieldsWithDefaults(Arrays.asList(amount, cron, from, to));

        binder.bind(from, "from");
        binder.bind(to, "to");

        formLayout.addComponents(amount, from, to, cron);
    }

    @Override
    public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
        FieldGroup binder = commitEvent.getFieldBinder();
        ExchangeTask taskData = new ExchangeTask();
        taskData.setUserId(SecurityUtils.getIdentityUserPrincipal().getId());
        taskData.setFromCcy(String.valueOf(binder.getField("from").getValue()));
        taskData.setToCcy(String.valueOf(binder.getField("to").getValue()));
        taskData.setAddedDatetime(LocalDateTime.now());
        taskData.setAmount(Double.valueOf(String.valueOf(((TextField)binder.getField("amount")).getConvertedValue())));
        taskData.setCron(String.valueOf(binder.getField("cron").getValue()));
        taskData.setActive(false);
        taskService.create(taskData);
    }

    @Override
    protected ButtonFactory.Mode getSubmitButtonMode() {
        return ButtonFactory.Mode.CREATE;
    }

}
