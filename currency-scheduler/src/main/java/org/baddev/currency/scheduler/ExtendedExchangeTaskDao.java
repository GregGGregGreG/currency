package org.baddev.currency.scheduler;

import org.baddev.currency.jooq.schema.tables.daos.ExchangeTaskDao;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.jooq.Configuration;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static org.baddev.currency.jooq.schema.Tables.USER;
import static org.baddev.currency.jooq.schema.Tables.USER_EXCHANGE_TASK;
import static org.baddev.currency.jooq.schema.tables.ExchangeTask.EXCHANGE_TASK;

/**
 * Created by IPotapchuk on 7/7/2016.
 */
@Repository("extendedExchangeTaskDao")
@Primary
public class ExtendedExchangeTaskDao extends ExchangeTaskDao {

    public ExtendedExchangeTaskDao() {
        super();
    }

    @Autowired
    public ExtendedExchangeTaskDao(Configuration configuration) {
        super(configuration);
    }

    public List<ExchangeTask> findForUser(String username){
        return DSL.using(configuration())
                .select(Arrays.asList(EXCHANGE_TASK.fields()))
                .from(USER.join(USER_EXCHANGE_TASK)
                        .on(USER.ID.eq(USER_EXCHANGE_TASK.USER_ID))
                        .join(EXCHANGE_TASK)
                        .on(USER_EXCHANGE_TASK.EXCHANGE_TASK_ID.eq(EXCHANGE_TASK.ID))
                ).where(USER.USERNAME.eq(username))
                .fetchInto(ExchangeTask.class);
    }

}
