/**
 * This class is generated by jOOQ
 */
package org.baddev.currency.exchange.dao.jooq.schema.tables.records;


import java.sql.Date;

import javax.annotation.Generated;

import org.baddev.currency.exchange.dao.jooq.schema.tables.ExchangeRate;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ExchangeRateRecord extends UpdatableRecordImpl<ExchangeRateRecord> implements Record6<Long, String, String, Date, Double, Long> {

	private static final long serialVersionUID = 2146755373;

	/**
	 * Setter for <code>exchanger.exchange_rate.id</code>.
	 */
	public void setId(Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_rate.id</code>.
	 */
	public Long getId() {
		return (Long) getValue(0);
	}

	/**
	 * Setter for <code>exchanger.exchange_rate.base_liter_code</code>.
	 */
	public void setBaseLiterCode(String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_rate.base_liter_code</code>.
	 */
	public String getBaseLiterCode() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>exchanger.exchange_rate.liter_code</code>.
	 */
	public void setLiterCode(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_rate.liter_code</code>.
	 */
	public String getLiterCode() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>exchanger.exchange_rate.exchange_date</code>.
	 */
	public void setExchangeDate(Date value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_rate.exchange_date</code>.
	 */
	public Date getExchangeDate() {
		return (Date) getValue(3);
	}

	/**
	 * Setter for <code>exchanger.exchange_rate.rate</code>.
	 */
	public void setRate(Double value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_rate.rate</code>.
	 */
	public Double getRate() {
		return (Double) getValue(4);
	}

	/**
	 * Setter for <code>exchanger.exchange_rate.exchange_id</code>.
	 */
	public void setExchangeId(Long value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_rate.exchange_id</code>.
	 */
	public Long getExchangeId() {
		return (Long) getValue(5);
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
	// Record6 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<Long, String, String, Date, Double, Long> fieldsRow() {
		return (Row6) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<Long, String, String, Date, Double, Long> valuesRow() {
		return (Row6) super.valuesRow();
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
		return ExchangeRate.EXCHANGE_RATE.BASE_LITER_CODE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return ExchangeRate.EXCHANGE_RATE.LITER_CODE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Date> field4() {
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
	public Field<Long> field6() {
		return ExchangeRate.EXCHANGE_RATE.EXCHANGE_ID;
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
		return getBaseLiterCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getLiterCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date value4() {
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
	public Long value6() {
		return getExchangeId();
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
		setBaseLiterCode(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeRateRecord value3(String value) {
		setLiterCode(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeRateRecord value4(Date value) {
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
	public ExchangeRateRecord value6(Long value) {
		setExchangeId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeRateRecord values(Long value1, String value2, String value3, Date value4, Double value5, Long value6) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
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
	public ExchangeRateRecord(Long id, String baseLiterCode, String literCode, Date exchangeDate, Double rate, Long exchangeId) {
		super(ExchangeRate.EXCHANGE_RATE);

		setValue(0, id);
		setValue(1, baseLiterCode);
		setValue(2, literCode);
		setValue(3, exchangeDate);
		setValue(4, rate);
		setValue(5, exchangeId);
	}
}
