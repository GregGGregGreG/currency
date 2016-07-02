/**
 * This class is generated by jOOQ
 */
package org.baddev.currency.jooq.schema.tables.records;


import org.baddev.currency.jooq.schema.tables.ExchangeRate;
import org.joda.time.LocalDate;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ExchangeRateRecord extends UpdatableRecordImpl<ExchangeRateRecord> implements Record5<Long, String, String, LocalDate, Double> {

    private static final long serialVersionUID = -241232909;

    /**
     * Setter for <code>exchanger.exchange_rate.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>exchanger.exchange_rate.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>exchanger.exchange_rate.base_ccy</code>.
     */
    public void setBaseCcy(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>exchanger.exchange_rate.base_ccy</code>.
     */
    public String getBaseCcy() {
        return (String) get(1);
    }

    /**
     * Setter for <code>exchanger.exchange_rate.ccy</code>.
     */
    public void setCcy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>exchanger.exchange_rate.ccy</code>.
     */
    public String getCcy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>exchanger.exchange_rate.exchange_date</code>.
     */
    public void setExchangeDate(LocalDate value) {
        set(3, value);
    }

    /**
     * Getter for <code>exchanger.exchange_rate.exchange_date</code>.
     */
    public LocalDate getExchangeDate() {
        return (LocalDate) get(3);
    }

    /**
     * Setter for <code>exchanger.exchange_rate.rate</code>.
     */
    public void setRate(Double value) {
        set(4, value);
    }

    /**
     * Getter for <code>exchanger.exchange_rate.rate</code>.
     */
    public Double getRate() {
        return (Double) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Long, String, String, LocalDate, Double> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Long, String, String, LocalDate, Double> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return ExchangeRate.EXCHANGE_RATE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return ExchangeRate.EXCHANGE_RATE.BASE_CCY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return ExchangeRate.EXCHANGE_RATE.CCY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDate> field4() {
        return ExchangeRate.EXCHANGE_RATE.EXCHANGE_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Double> field5() {
        return ExchangeRate.EXCHANGE_RATE.RATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getBaseCcy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getCcy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate value4() {
        return getExchangeDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double value5() {
        return getRate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExchangeRateRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExchangeRateRecord value2(String value) {
        setBaseCcy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExchangeRateRecord value3(String value) {
        setCcy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExchangeRateRecord value4(LocalDate value) {
        setExchangeDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExchangeRateRecord value5(Double value) {
        setRate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExchangeRateRecord values(Long value1, String value2, String value3, LocalDate value4, Double value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ExchangeRateRecord
     */
    public ExchangeRateRecord() {
        super(ExchangeRate.EXCHANGE_RATE);
    }

    /**
     * Create a detached, initialised ExchangeRateRecord
     */
    public ExchangeRateRecord(Long id, String baseCcy, String ccy, LocalDate exchangeDate, Double rate) {
        super(ExchangeRate.EXCHANGE_RATE);

        set(0, id);
        set(1, baseCcy);
        set(2, ccy);
        set(3, exchangeDate);
        set(4, rate);
    }
}
