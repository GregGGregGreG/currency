package org.baddev.currency.dao.fetcher.impl;

import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.dao.fetcher.ExchangeRateDao;
import org.baddev.currency.dao.utils.ConverterUtils;
import org.baddev.currency.jooq.schema.tables.records.ExchangeRateRecord;
import org.joda.time.LocalDate;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
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
                    .baseCurrencyCode(record.getBaseLiterCode())
                    .currencyCode(record.getLiterCode())
                    .date(ConverterUtils.fromSqlDate(record.getExchangeDate()))
                    .rate(record.getRate())
                    .build();
        }
    }

    @Override
    public void save(BaseExchangeRate record) {
        dsl.insertInto(EXCHANGE_RATE)
                .set(EXCHANGE_RATE.BASE_LITER_CODE, record.getBaseCurrencyCode())
                .set(EXCHANGE_RATE.LITER_CODE, record.getCcy())
                .set(EXCHANGE_RATE.EXCHANGE_DATE, ConverterUtils.toSqlDate(record.getDate()))
                .set(EXCHANGE_RATE.RATE, record.getRate())
                .execute();
    }

    @Override
    public void remove(Long id) {
        ExchangeRateRecord rec = dsl.selectFrom(EXCHANGE_RATE)
                .where(EXCHANGE_RATE.ID.eq(id))
                .fetchOne();
        if (rec != null)
            rec.delete();
        else
            log.warn("Record with id [{}] doesn't exist.", id);
    }

    @Override
    public BaseExchangeRate load(Long id) {
        return dsl.selectFrom(EXCHANGE_RATE)
                .where(EXCHANGE_RATE.ID.eq(id))
                .fetch(new BaseExchangeRateMapper())
                .get(0);
    }

    @Override
    public Collection<BaseExchangeRate> loadAll() {
        return dsl.selectFrom(EXCHANGE_RATE)
                .fetch(new BaseExchangeRateMapper());
    }

    @Override
    public void saveAll(Collection<BaseExchangeRate> rates) {
        dsl.batchInsert(toRecords(rates)).execute();
        log.info("All fetched rates have been saved");
    }

    @Override
    public Collection<BaseExchangeRate> loadByDate(LocalDate date) {
        return dsl.selectFrom(EXCHANGE_RATE)
                .where(EXCHANGE_RATE.EXCHANGE_DATE.eq(ConverterUtils.toSqlDate(date)))
                .fetch(new BaseExchangeRateMapper());
    }

    private List<ExchangeRateRecord> toRecords(Collection<BaseExchangeRate> rates) {
        return rates.stream()
                .map(rate -> new ExchangeRateRecord(rate.getId(),
                        rate.getBaseCurrencyCode(),
                        rate.getCcy(),
                        ConverterUtils.toSqlDate(rate.getDate()),
                        rate.getRate()))
                .collect(Collectors.toList());
     }
}
