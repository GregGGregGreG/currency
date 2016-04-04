package org.baddev.currency.ui.currency.jooq.config;

import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Created by IPotapchuk on 2/15/2016.
 */
public class SpringTransactionProvider implements TransactionProvider{

    private static final Logger log = LoggerFactory.getLogger(SpringTransactionProvider.class);

    private DataSourceTransactionManager txMgr;

    public DataSourceTransactionManager getTxMgr() {
        return txMgr;
    }

    public void setTxMgr(DataSourceTransactionManager txMgr) {
        this.txMgr = txMgr;
    }

    @Override
    public void begin(TransactionContext ctx) {
        log.info("Beginning transaction");
        // This TransactionProvider behaves like jOOQ's DefaultTransactionProvider,
        // which supports nested transactions using Savepoints
        TransactionStatus tx = txMgr.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_NESTED));
        ctx.transaction(new SpringTransaction(tx));
    }

    @Override
    public void commit(TransactionContext ctx) {
        log.info("Committing transaction");
        txMgr.commit(((SpringTransaction) ctx.transaction()).tx);
    }

    @Override
    public void rollback(TransactionContext ctx) {
        log.info("Rolling back transaction");
        txMgr.rollback(((SpringTransaction) ctx.transaction()).tx);
    }

}
