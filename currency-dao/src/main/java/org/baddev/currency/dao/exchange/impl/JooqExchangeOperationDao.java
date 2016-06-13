package org.baddev.currency.dao.exchange.impl;

import org.baddev.currency.core.exchange.entity.ExchangeOperation;
import org.baddev.currency.dao.exchange.ExchangeOperationDao;
import org.baddev.currency.dao.utils.ConverterUtils;
import org.baddev.currency.jooq.schema.tables.records.ExchangeOperationRecord;
import org.joda.time.LocalDate;
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
                    .date(ConverterUtils.fromSqlDate(record.getDate()))
                    .amount(record.getFromAmount())
                    .exchangedAmount(record.getToAmount())
                    .from(record.getFromCurrencyCode())
                    .to(record.getToCurrencyCode())
                    .build();
        }
    }

    @Override
    public void save(ExchangeOperation record) {
        dsl.insertInto(EXCHANGE_OPERATION)
                .set(EXCHANGE_OPERATION.FROM_CURRENCY_CODE, record.getAmountCurrencyCode())
                .set(EXCHANGE_OPERATION.TO_CURRENCY_CODE, record.getExchangedAmountCurrencyCode())
                .set(EXCHANGE_OPERATION.FROM_AMOUNT, record.getAmount())
                .set(EXCHANGE_OPERATION.TO_AMOUNT, record.getExchangedAmount())
                .set(EXCHANGE_OPERATION.DATE, ConverterUtils.toSqlDate(record.getDate()))
                .execute();
    }

    @Override
    public void remove(Long id) {
        ExchangeOperationRecord rec = dsl.selectFrom(EXCHANGE_OPERATION)
                .where(EXCHANGE_OPERATION.ID.eq(id))
                .fetchOne();
        if(rec != null)
            rec.delete();
        else
            log.warn("Record with id [{}] not found.", id);

    }

    @Override
    public ExchangeOperation load(Long id) {
        return dsl.selectFrom(EXCHANGE_OPERATION)
                .where(EXCHANGE_OPERATION.ID.eq(id))
                .fetch(new ExchangeOperationMapper())
                .get(0);
    }

    @Override
    public Collection<ExchangeOperation> loadAll() {
        return dsl.selectFrom(EXCHANGE_OPERATION)
                .fetch(new ExchangeOperationMapper());
    }

    @Override
    public Collection<ExchangeOperation> loadByDate(LocalDate date) {
        return dsl.selectFrom(EXCHANGE_OPERATION)
                .where(EXCHANGE_OPERATION.DATE.eq(ConverterUtils.toSqlDate(date)))
                .fetch(new ExchangeOperationMapper());
    }
}
