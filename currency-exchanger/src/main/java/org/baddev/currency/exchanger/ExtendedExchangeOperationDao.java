package org.baddev.currency.exchanger;

import org.baddev.currency.jooq.schema.tables.daos.ExchangeOperationDao;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.baddev.currency.jooq.schema.tables.records.ExchangeOperationRecord;
import org.jooq.Configuration;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static org.baddev.currency.jooq.schema.tables.ExchangeOperation.EXCHANGE_OPERATION;
import static org.baddev.currency.jooq.schema.tables.User.USER;
import static org.baddev.currency.jooq.schema.tables.UserExchangeOperation.USER_EXCHANGE_OPERATION;

/**
 * Created by IPotapchuk on 7/7/2016.
 */
@Repository("extendedExchangeOperationDao")
@Primary
public class ExtendedExchangeOperationDao extends ExchangeOperationDao {

    public ExtendedExchangeOperationDao() {
        super();
    }

    @Autowired
    public ExtendedExchangeOperationDao(Configuration configuration) {
        super(configuration);
    }

    public List<ExchangeOperation> findForUser(String username) {
        return DSL.using(configuration())
                .select(Arrays.asList(EXCHANGE_OPERATION.fields()))
                .from(USER.join(USER_EXCHANGE_OPERATION)
                        .on(USER.ID.eq(USER_EXCHANGE_OPERATION.USER_ID))
                        .join(EXCHANGE_OPERATION)
                        .on(USER_EXCHANGE_OPERATION.EXCHANGE_OPERATION_ID.eq(EXCHANGE_OPERATION.ID))
                ).where(USER.USERNAME.eq(username))
                .fetchInto(ExchangeOperation.class);
    }

    public ExchangeOperation insertReturning(ExchangeOperation exchangeOperation) {
        return DSL.using(configuration())
                .insertInto(EXCHANGE_OPERATION)
                .set(exchangeOperation.into(new ExchangeOperationRecord()))
                .returning()
                .fetchOne()
                .into(ExchangeOperation.class);
    }

}
