package org.baddev.currency.scheduler;

import org.baddev.currency.core.exchange.entity.ExchangeOperation;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
public class CronExchangeOperation extends ExchangeOperation {

    private String cron;

    public static final String P_CRON = "cron";

    public CronExchangeOperation() {
    }

    public CronExchangeOperation(String cron, ExchangeOperation operation) {
        super.setId(operation.getId());
        super.setAmount(operation.getAmount());
        super.setAmountCurrencyCode(operation.getAmountCurrencyCode());
        super.setExchangedAmount(operation.getExchangedAmount());
        super.setExchangedAmountCurrencyCode(operation.getExchangedAmountCurrencyCode());
        super.setDate(operation.getDate());
        this.cron = cron;
    }

    public String getCron() {
        return cron;
    }

}
