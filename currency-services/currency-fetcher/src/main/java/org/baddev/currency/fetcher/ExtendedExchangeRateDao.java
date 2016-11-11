package org.baddev.currency.fetcher;

import org.baddev.currency.jooq.schema.tables.daos.ExchangeRateDao;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.jooq.Configuration;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import static org.baddev.currency.jooq.schema.tables.ExchangeRate.EXCHANGE_RATE;

/**
 * Created by IPotapchuk on 7/4/2016.
 */
@Repository("extendedExchangeRateDao")
@Primary
public class ExtendedExchangeRateDao extends ExchangeRateDao {

    public ExtendedExchangeRateDao() {
        super();
    }

    @Autowired
    public ExtendedExchangeRateDao(Configuration configuration) {
        super(configuration);
    }

    public Collection<ExchangeRate> findLastRates() {
        return DSL.using(configuration()).selectFrom(EXCHANGE_RATE).where(
                EXCHANGE_RATE.EXCHANGE_DATE.eq(
                        DSL.using(configuration()).select(DSL.max(EXCHANGE_RATE.EXCHANGE_DATE)).from(EXCHANGE_RATE)
                )).fetchInto(ExchangeRate.class);
    }

}
