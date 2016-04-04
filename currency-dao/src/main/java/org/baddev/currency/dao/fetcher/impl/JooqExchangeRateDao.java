package org.baddev.currency.dao.fetcher.impl;

import org.baddev.currency.dao.fetcher.ExchangeRateDao;
import org.baddev.currency.dao.utils.ConverterUtils;
import org.baddev.currency.fetcher.entity.ExchangeRate;
import org.baddev.currency.jooq.schema.tables.records.ExchangeRateRecord;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.baddev.currency.jooq.schema.Tables.EXCHANGE_RATE;

/**
 * Created by IPotapchuk on 4/4/2016.
 */
@Repository("exchangeRateDao")
public class JooqExchangeRateDao implements ExchangeRateDao {

    private static final Logger log = LoggerFactory.getLogger(JooqExchangeRateDao.class);

    @Autowired
    private DSLContext dsl;

    @Override
    public void save(ExchangeRate record) {
        dsl.insertInto(EXCHANGE_RATE)
                .set(EXCHANGE_RATE.BASE_LITER_CODE, record.getBaseLiterCode())
                .set(EXCHANGE_RATE.LITER_CODE, record.getLiterCode())
                .set(EXCHANGE_RATE.EXCHANGE_DATE, ConverterUtils.toSqlDate(record.getExchangeDate()))
                .set(EXCHANGE_RATE.RATE, record.getRate())
                .execute();
    }

    @Override
    public void remove(Long id) {
        ExchangeRateRecord rec = dsl.selectFrom(EXCHANGE_RATE)
                .where(EXCHANGE_RATE.ID.eq(id))
                .fetchOne();
        if(rec != null)
            rec.delete();
        else
            log.warn("Record with id [{}] doesn't exist.", id);
    }

    @Override
    public ExchangeRate load(Long id) {
        return dsl.selectFrom(EXCHANGE_RATE)
                .where(EXCHANGE_RATE.ID.eq(id))
                .fetchInto(ExchangeRate.class)
                .get(0);
    }

    @Override
    public Collection<ExchangeRate> loadAll() {
        return dsl.selectFrom(EXCHANGE_RATE)
                .fetchInto(ExchangeRate.class);
    }

    @Override
    public void saveAll(Collection<ExchangeRate> rates) {
        dsl.batchInsert(toRecords(rates)).execute();
    }

    private List<ExchangeRateRecord> toRecords(Collection<ExchangeRate> rates){
        List<ExchangeRateRecord> recs = new ArrayList<>();
        for(ExchangeRate rate : rates){
            recs.add(new ExchangeRateRecord(rate.getId(),
                    rate.getBaseLiterCode(),
                    rate.getLiterCode(),
                    ConverterUtils.toSqlDate(rate.getExchangeDate()),
                    rate.getRate()));
        }
        return recs;
    }
}
