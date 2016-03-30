package org.baddev.currency.exchange.dao.jooq.config;

import org.jooq.Transaction;
import org.springframework.transaction.TransactionStatus;

/**
 * Created by IPotapchuk on 2/15/2016.
 */
public class SpringTransaction implements Transaction{
    final TransactionStatus tx;

    public SpringTransaction(TransactionStatus tx) {
        this.tx = tx;
    }

}
