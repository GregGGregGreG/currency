package org.baddev.currency.dao.fetcher.impl;

import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.dao.fetcher.ExchangeRateDao;
import org.baddev.currency.jooq.schema.tables.records.ExchangeRateRecord;
import org.joda.time.LocalDate;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.baddev.currency.jooq.schema.Tables.EXCHANGE_RATE;

/**
 * Created by IPotapchuk on 4/4/2016.
 */
@Repository("exchangeRateDao")
public class JooqExchangeRateDao implements ExchangeRateDao {

    private static final Logger log = LoggerFactory.getLogger(JooqExchangeRateDao.class);

    @Autowired
    private DSLContext dsl;

    private static final class BaseExchangeRateMapper
            implements RecordMapper<ExchangeRateRecord, BaseExchangeRate> {
        @Override
        public BaseExchangeRate map(ExchangeRateRecord record) {
            return BaseExchangeRate.newBuilder()
                    .id(record.getId())
                    .baseCcy(record.getBaseCcy())
                    .ccy(record.getCcy())
                    .date(record.getExchangeDate())
                    .rate(record.getRate())
                    .build();
        }
    }

    @Override
    public void save(BaseExchangeRate record) {
        dsl.insertInto(EXCHANGE_RATE)
                .set(EXCHANGE_RATE.BASE_CCY, record.getBaseCcy())
                .set(EXCHANGE_RATE.CCY, record.getCcy())
                .set(EXCHANGE_RATE.EXCHANGE_DATE, record.getDate())
                .set(EXCHANGE_RATE.RATE, record.getRate())
                .execute();
    }

    @Override
    public void remove(Long id) {
        dsl.deleteFrom(EXCHANGE_RATE).where(EXCHANGE_RATE.ID.eq(id)).execute();
    }

    @Override
    public BaseExchangeRate load(Long id) {
        return dsl.selectFrom(EXCHANGE_RATE).where(EXCHANGE_RATE.ID.eq(id)).fetchOne(new BaseExchangeRateMapper());
    }

    @Override
    public Collection<BaseExchangeRate> loadAll() {
        return dsl.selectFrom(EXCHANGE_RATE).fetch(new BaseExchangeRateMapper());
    }

    @Override
    public void save(Collection<BaseExchangeRate> rates) {
        dsl.batchInsert(toRecords(rates)).execute();
    }

    @Override
    public Collection<BaseExchangeRate> findByDate(LocalDate date) {
        return dsl.selectFrom(EXCHANGE_RATE)
                .where(EXCHANGE_RATE.EXCHANGE_DATE.eq(date))
                .fetch(new BaseExchangeRateMapper());
    }

    @Override
    public Collection<BaseExchangeRate> findForCcy(String ccy) {
        return dsl.selectFrom(EXCHANGE_RATE)
                .where(EXCHANGE_RATE.CCY.eq(ccy))
                .fetch(new BaseExchangeRateMapper());
    }

    @Override
    public Collection<BaseExchangeRate> findLastRates() {
        return dsl.selectFrom(EXCHANGE_RATE)
                .where(EXCHANGE_RATE.EXCHANGE_DATE.eq(
                        dsl.select(DSL.max(EXCHANGE_RATE.EXCHANGE_DATE)).from(EXCHANGE_RATE)
                )).fetch(new BaseExchangeRateMapper());
    }

    private List<ExchangeRateRecord> toRecords(Collection<BaseExchangeRate> rates) {
        return rates.stream()
                .map(rate -> new ExchangeRateRecord(rate.getId(),
                        rate.getBaseCcy(),
                        rate.getCcy(),
                        rate.getDate(),
                        rate.getRate()))
                .collect(Collectors.toList());
    }

}
