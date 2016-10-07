package org.baddev.currency.scheduler.exchange.task;

import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.core.event.Notifier;
import org.baddev.currency.exchanger.ExchangerService;
import org.baddev.currency.fetcher.service.ExchangeRateService;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;

public class NotifiableExchangeTask extends AbstractCallbackTask<ExchangeCompletionEvent> {

    private IExchangeTask taskData;
    private ExchangerService exchangerService;
    private ExchangeRateService exchangeRateService;
    private boolean success;

    public NotifiableExchangeTask(Notifier n, IExchangeTask taskData, ExchangerService exchangerService,
                                  ExchangeRateService exchangeRateService) {
        super(n);
        this.taskData = taskData;
        this.exchangerService = exchangerService;
        this.exchangeRateService = exchangeRateService;
    }

    public NotifiableExchangeTask(Notifier n, ExchangerService exchangerService, ExchangeRateService exchangeRateService) {
        super(n);
        this.exchangerService = exchangerService;
        this.exchangeRateService = exchangeRateService;
    }

    @Required
    public void setTaskData(IExchangeTask taskData) {
        this.taskData = taskData;
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
        Optional<IExchangeOperation> fulfilled = exchangerService.exchange(exchOp, exchangeRateService.fetchCurrent());
        return new ExchangeCompletionEvent(this, fulfilled.orElseGet(() -> {
            success = false;
            return exchOp;
        }), success);
    }
}