package org.baddev.currency.exchanger.task;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.baddev.common.schedulling.task.AbstractCallbackTask;
import org.baddev.currency.core.api.ExchangeRateService;
import org.baddev.currency.core.api.ExchangerService;
import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Required;

@RequiredArgsConstructor
public class NotifiableExchangeTask extends AbstractCallbackTask<ExchangeCompletionEvent> {

    @Getter
    private IExchangeTask taskData;
    private final ExchangerService exchangerService;
    private final ExchangeRateService exchangeRateService;

    @Required
    public void setTaskData(@NonNull IExchangeTask taskData) {
        this.taskData = taskData;
        setId(taskData.getId());
    }

    @Override
    protected ExchangeCompletionEvent beforeCallback() {
        IExchangeOperation exchOp = new ExchangeOperation();
        exchOp.setUserId(taskData.getUserId());
        exchOp.setFromCcy(taskData.getFromCcy());
        exchOp.setToCcy(taskData.getToCcy());
        exchOp.setFromAmount(taskData.getAmount());
        exchOp.setRatesDate(LocalDate.now());
        return new ExchangeCompletionEvent(this, exchangerService.exchange(exchOp, exchangeRateService.fetchCurrent()));
    }
}