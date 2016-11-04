package org.baddev.currency.exchanger.impl;

import org.baddev.currency.core.action.ExchangeAction;
import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.joda.time.LocalDateTime;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by IPotapchuk on 9/30/2016.
 */
@Component
@Primary
public class ExchangeActionImpl implements ExchangeAction {

    @Override
    public IExchangeOperation exchange(IExchangeOperation operation, Collection<? extends IExchangeRate> rates) {
        Objects.requireNonNull(operation);
        ExchangeOperation op = operation.into(new ExchangeOperation());
        op.setPerformDatetime(LocalDateTime.now());

        double toRate = filterRate(rates, op.getToCcy());
        if (toRate == -1) throw new ServiceException("Exchange Error. Failed to find target rate: " + op.getToCcy());
        if (op.getFromCcy().equals("UAH")) {
            op.setToAmount(toRate * op.getFromAmount());
        } else {
            double fromRate = filterRate(rates, op.getFromCcy());
            if (fromRate == -1) throw new ServiceException("Exchange Error. Failed to find base rate: " + op.getFromCcy());
            double inDefaultBase = fromRate * op.getFromAmount();
            op.setToAmount(inDefaultBase / toRate);
        }
        return op;
    }

    private static <E extends IExchangeRate> double filterRate(Collection<E> rates, String ccy) {
        return rates.stream()
                .filter(r -> r.getCcy().equals(ccy))
                .mapToDouble(IExchangeRate::getRate)
                .findFirst()
                .orElse(-1);
    }
}
