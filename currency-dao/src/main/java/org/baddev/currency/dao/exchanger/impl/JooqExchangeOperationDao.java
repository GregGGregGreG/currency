package org.baddev.currency.dao.exchanger.impl;

import org.baddev.currency.core.exchanger.entity.ExchangeOperation;
import org.baddev.currency.dao.exchanger.ExchangeOperationDao;
import org.baddev.currency.jooq.schema.tables.records.ExchangeOperationRecord;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import static org.baddev.currency.jooq.schema.Tables.EXCHANGE_OPERATION;

/**
 * Created by IPotapchuk on 3/15/2016.
 */
@Repository("exchangeOperationDao")
public class JooqExchangeOperationDao implements ExchangeOperationDao {

    private static final Logger log = LoggerFactory.getLogger(JooqExchangeOperationDao.class);

    @Autowired
    private DSLContext dsl;

    private static final class ExchangeOperationMapper
            implements RecordMapper<ExchangeOperationRecord, ExchangeOperation> {

        @Override
        public ExchangeOperation map(ExchangeOperationRecord record) {
            return ExchangeOperation.newBuilder()
                    .id(record.getId())
                    .ratesDate(record.getRatesDate())
                    .performDate(record.getPerformDatetime())
                    .from(record.getFromCcy())
                    .amount(record.getFromAmount())
                    .to(record.getToCcy())
                    .exchangedAmount(record.getToAmount())
                    .build();
        }
    }

    @Override
    public void save(ExchangeOperation record) {
        dsl.insertInto(EXCHANGE_OPERATION)
                .set(EXCHANGE_OPERATION.FROM_CCY, record.getFromCcy())
                .set(EXCHANGE_OPERATION.TO_CCY, record.getToCcy())
                .set(EXCHANGE_OPERATION.FROM_AMOUNT, record.getAmount())
                .set(EXCHANGE_OPERATION.TO_AMOUNT, record.getExchangedAmount())
                .set(EXCHANGE_OPERATION.RATES_DATE, record.getRatesDate())
                .set(EXCHANGE_OPERATION.PERFORM_DATETIME, record.getPerformDate())
                .execute();
    }

    @Override
    public void remove(Long id) {
        dsl.deleteFrom(EXCHANGE_OPERATION).where(EXCHANGE_OPERATION.ID.eq(id)).execute();
    }

    @Override
    public ExchangeOperation load(Long id) {
        return dsl.selectFrom(EXCHANGE_OPERATION)
                .where(EXCHANGE_OPERATION.ID.eq(id))
                .fetchOne(new ExchangeOperationMapper());
    }

    @Override
    public Collection<ExchangeOperation> loadAll() {
        return dsl.selectFrom(EXCHANGE_OPERATION).fetch(new ExchangeOperationMapper());
    }

    @Override
    public Collection<ExchangeOperation> loadByRatesDate(LocalDate date) {
        return dsl.selectFrom(EXCHANGE_OPERATION)
                .where(EXCHANGE_OPERATION.RATES_DATE.eq(date))
                .fetch(new ExchangeOperationMapper());
    }

    @Override
    public Collection<ExchangeOperation> loadByPerformDateTime(LocalDateTime dateTime){
        return dsl.selectFrom(EXCHANGE_OPERATION)
                .where(EXCHANGE_OPERATION.PERFORM_DATETIME.eq(dateTime))
                .fetch(new ExchangeOperationMapper());
    }
}
