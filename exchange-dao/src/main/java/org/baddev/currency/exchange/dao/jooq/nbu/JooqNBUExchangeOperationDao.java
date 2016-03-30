package org.baddev.currency.exchange.dao.jooq.nbu;

import static org.baddev.currency.exchange.dao.jooq.schema.tables.ExchangeOperation.*;
import static org.baddev.currency.exchange.dao.jooq.schema.tables.ExchangeRate.*;

import org.baddev.currency.exchange.dao.ExchangeOperationDao;
import org.baddev.currency.exchange.dao.jooq.schema.tables.records.ExchangeOperationRecord;
import org.baddev.currency.exchange.dao.jooq.schema.tables.records.ExchangeRateRecord;
import org.baddev.currency.exchange.dao.utils.ConverterUtils;
import org.baddev.currency.exchange.entity.ExchangeOperation;
import org.baddev.currency.fetcher.entity.ExchangeRate;
import org.baddev.currency.fetcher.impl.nbu.entity.NBUExchangeRate;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IPotapchuk on 3/15/2016.
 */
@Repository("NBUDao")
public class JooqNBUExchangeOperationDao implements ExchangeOperationDao {

    private static final Logger log = LoggerFactory.getLogger(JooqNBUExchangeOperationDao.class);

    @Autowired
    private DSLContext dsl;

    private static final class RateMapper implements RecordMapper<ExchangeRateRecord, ExchangeRate> {
        @Override
        public ExchangeRate map(final ExchangeRateRecord record) {
            return NBUExchangeRate.newBuilder()
                    .id(record.getId())
                    .baseCurrencyLiterCode(record.getBaseLiterCode())
                    .literCode(record.getLiterCode())
                    .rate(record.getRate())
                    .exchangeDate(ConverterUtils.fromSqlDate(record.getExchangeDate()))
                    .build();
        }
    }

    private static final class OperationMapper implements RecordMapper<ExchangeOperationRecord, ExchangeOperation> {

        private List<ExchangeRateRecord> rateRecs;

        public OperationMapper(List<ExchangeRateRecord> rates) {
            this.rateRecs = rates;
        }

        @Override
        public ExchangeOperation map(final ExchangeOperationRecord record) {
//            return ExchangeOperation.newBuilder()
//                    .amount(record.getFromAmount())
//                    .from(record.getFromCurrencyCode())
//                    .to(record.getToCurrencyCode())
//                    .exchangedAmount(record.getToAmount())
//                    .rates(filter(record.getId(), rateRecs))
//                    .date(ConverterUtils.fromSqlDate(record.getDate()))
//                    .build();
            return null;
        }

        private List<ExchangeRate> filter(Long id, List<ExchangeRateRecord> all) {
            List<ExchangeRate> result = new ArrayList<>();
            for (ExchangeRateRecord rec : all) {
                if (rec.getExchangeId().equals(id)) {
                    result.add(NBUExchangeRate.newBuilder()
                            .id(rec.getId())
                            .baseCurrencyLiterCode(rec.getBaseLiterCode())
                            .literCode(rec.getLiterCode())
                            .exchangeDate(ConverterUtils.fromSqlDate(rec.getExchangeDate()))
                            .rate(rec.getRate())
                            .build());
                }
            }
            return result;
        }
    }

    @Override
    public void save(ExchangeOperation record) {
//        ExchangeOperationRecord rec = dsl.insertInto(EXCHANGE_OPERATION)
//                .set(EXCHANGE_OPERATION.FROM_CURRENCY_CODE, record.getAmountLiterCode())
//                .set(EXCHANGE_OPERATION.TO_CURRENCY_CODE, record.getExchangedAmountLiterCode())
//                .set(EXCHANGE_OPERATION.FROM_AMOUNT, record.getAmount())
//                .set(EXCHANGE_OPERATION.TO_AMOUNT, record.getExchangedAmount())
//                .set(EXCHANGE_OPERATION.DATE, record.getDate())
//                .returning(EXCHANGE_OPERATION.ID)
//                .fetchOne();
//        log.info("Exchange operation saved with id [{}]", rec.getId());
//        for (ExchangeRate curEx : record.getExchangeRates()) {
//            dsl.insertInto(EXCHANGE_RATE)
//                    .set(EXCHANGE_RATE.EXCHANGE_ID, rec.getId())
//                    .set(EXCHANGE_RATE.BASE_LITER_CODE, curEx.getBaseLiterCode())
//                    .set(EXCHANGE_RATE.EXCHANGE_DATE, ConverterUtils.toSqlDate(curEx.getExchangeDate()))
//                    .set(EXCHANGE_RATE.LITER_CODE, curEx.getLiterCode())
//                    .set(EXCHANGE_RATE.RATE, curEx.getRate())
//                    .execute();
//            log.info("Exchange rate saved for exchange operation with id [{}]", rec.getId());
    }

    @Override
    public void remove(Long id) {
        dsl.deleteFrom(EXCHANGE_OPERATION)
                .where(EXCHANGE_OPERATION.ID.eq(id))
                .execute();
        dsl.deleteFrom(EXCHANGE_RATE)
                .where(EXCHANGE_RATE.EXCHANGE_ID.eq(id))
                .execute();
        log.info("Removed [{}]", id);
    }

    @Override
    public ExchangeOperation load(Long id) {
        ExchangeOperationRecord rec = dsl.selectFrom(EXCHANGE_OPERATION)
                .where(EXCHANGE_OPERATION.ID.eq(id))
                .fetchAny();
        List<ExchangeRate> exchanges = dsl.selectFrom(EXCHANGE_RATE)
                .where(EXCHANGE_RATE.EXCHANGE_ID.eq(id))
                .fetch(new RateMapper());
        log.info("Exchange found: [{}], with [{}] entries", rec != null, exchanges.size());
//        return ExchangeOperation.newBuilder()
//                .id(rec.getId())
//                .amount(rec.getFromAmount())
//                .exchangedAmount(rec.getToAmount())
//                .from(rec.getFromCurrencyCode())
//                .to(rec.getToCurrencyCode())
//                .rates(exchanges)
//                .build();
        return null;
    }

    @Override
    public Collection<ExchangeOperation> loadAll() {
        final List<ExchangeRateRecord> rateRecs = dsl.selectFrom(EXCHANGE_RATE)
                .orderBy(EXCHANGE_RATE.ID)
                .fetch();
        return dsl.selectFrom(EXCHANGE_OPERATION)
                .orderBy(EXCHANGE_OPERATION.ID)
                .fetch()
                .map(new OperationMapper(rateRecs));
    }

}
