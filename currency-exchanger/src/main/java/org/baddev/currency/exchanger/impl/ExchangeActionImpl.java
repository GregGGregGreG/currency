package org.baddev.currency.exchanger.impl;

import org.baddev.currency.core.action.ExchangeAction;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by IPotapchuk on 9/30/2016.
 */
@Component
public class ExchangeActionImpl implements ExchangeAction{

    private static final Logger log = LoggerFactory.getLogger(ExchangeActionImpl.class);

    @Override
    public Optional<IExchangeOperation> exchange(IExchangeOperation operation, Collection<? extends IExchangeRate> rates) {
        Objects.requireNonNull(operation);
        ExchangeOperation op = operation.into(new ExchangeOperation());
        op.setPerformDatetime(LocalDateTime.now());

        double toRate = filterRate(rates, op.getToCcy());
        if(toRate == -1) return Optional.empty();
        if (op.getFromCcy().equals("UAH")) {
            op.setToAmount(toRate * op.getFromAmount());
        } else {
            double fromRate = filterRate(rates, op.getFromCcy());
            if(fromRate == -1) return Optional.empty();
            double inDefaultBase = fromRate * op.getFromAmount();
            op.setToAmount(inDefaultBase / toRate);
        }
        log.info("Exchanged amount: [{}]{}, userId=[{}]", op.getToAmount(), op.getToCcy(), op.getUserId());
        return Optional.of(op);
    }

    private static <E extends IExchangeRate> double filterRate(Collection<E> rates, String ccy) {
        return rates.stream()
                .filter(r -> r.getCcy().equals(ccy))
                .mapToDouble(IExchangeRate::getRate)
                .findFirst()
                .orElse(-1);
    }
}
