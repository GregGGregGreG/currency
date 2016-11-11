package org.baddev.currency.exchanger.task;

import org.baddev.common.schedulling.task.AbstractCallbackTask;
import org.baddev.currency.core.CoreErrorHandler;
import org.baddev.currency.core.api.ExchangeRateService;
import org.baddev.currency.core.api.ExchangerService;
import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

public class NotifiableExchangeTask extends AbstractCallbackTask<ExchangeCompletionEvent> {

    private IExchangeTask taskData;
    private final ExchangerService exchangerService;
    private final ExchangeRateService exchangeRateService;
    private boolean success = true;

    public NotifiableExchangeTask(IExchangeTask taskData, ExchangerService exchangerService, ExchangeRateService exchangeRateService) {
        this(exchangerService, exchangeRateService);
        setTaskData(taskData);
    }

    public NotifiableExchangeTask(ExchangerService exchangerService, ExchangeRateService exchangeRateService) {
        Assert.notNull(exchangerService, "exchangerService must be a non-null value");
        Assert.notNull(exchangeRateService, "exchangeRateService must be a non-null value");
        this.exchangerService = exchangerService;
        this.exchangeRateService = exchangeRateService;
    }

    @Required
    public void setTaskData(IExchangeTask taskData) {
        Assert.notNull(taskData, "taskData must be a non-null value");
        Assert.notNull(taskData.getId(), "taskData must have an id assigned");
        this.taskData = taskData;
        setId(taskData.getId());
    }

    public IExchangeTask getTaskData() {
        return taskData;
    }

    @Override
    protected ExchangeCompletionEvent beforeCallback() {
        IExchangeOperation exchOp = new ExchangeOperation();
        exchOp.setUserId(taskData.getUserId());
        exchOp.setFromCcy(taskData.getFromCcy());
        exchOp.setToCcy(taskData.getToCcy());
        exchOp.setFromAmount(taskData.getAmount());
        exchOp.setRatesDate(LocalDate.now());
        IExchangeOperation result = null;
        try {
            result = exchangerService.exchange(exchOp, exchangeRateService.fetchCurrent());
        } catch (Exception e){
            success = false;
            new CoreErrorHandler().handle(e);
        }
        return new ExchangeCompletionEvent(this, result, success);
    }
}