/**
 * This class is generated by jOOQ
 */
package org.baddev.currency.jooq.schema.tables.records;


import java.sql.Date;

import javax.annotation.Generated;

import org.baddev.currency.jooq.schema.tables.ExchangeOperation;
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
public class ExchangeOperationRecord extends UpdatableRecordImpl<ExchangeOperationRecord> implements Record6<Long, String, String, Double, Double, Date> {

	private static final long serialVersionUID = -1386080707;

	/**
	 * Setter for <code>exchanger.exchange_operation.id</code>.
	 */
	public void setId(Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_operation.id</code>.
	 */
	public Long getId() {
		return (Long) getValue(0);
	}

	/**
	 * Setter for <code>exchanger.exchange_operation.from_currency_code</code>.
	 */
	public void setFromCurrencyCode(String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_operation.from_currency_code</code>.
	 */
	public String getFromCurrencyCode() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>exchanger.exchange_operation.to_currency_code</code>.
	 */
	public void setToCurrencyCode(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_operation.to_currency_code</code>.
	 */
	public String getToCurrencyCode() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>exchanger.exchange_operation.from_amount</code>.
	 */
	public void setFromAmount(Double value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_operation.from_amount</code>.
	 */
	public Double getFromAmount() {
		return (Double) getValue(3);
	}

	/**
	 * Setter for <code>exchanger.exchange_operation.to_amount</code>.
	 */
	public void setToAmount(Double value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_operation.to_amount</code>.
	 */
	public Double getToAmount() {
		return (Double) getValue(4);
	}

	/**
	 * Setter for <code>exchanger.exchange_operation.date</code>.
	 */
	public void setDate(Date value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_operation.date</code>.
	 */
	public Date getDate() {
		return (Date) getValue(5);
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
	public Row6<Long, String, String, Double, Double, Date> fieldsRow() {
		return (Row6) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<Long, String, String, Double, Double, Date> valuesRow() {
		return (Row6) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Long> field1() {
		return ExchangeOperation.EXCHANGE_OPERATION.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return ExchangeOperation.EXCHANGE_OPERATION.FROM_CURRENCY_CODE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return ExchangeOperation.EXCHANGE_OPERATION.TO_CURRENCY_CODE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field4() {
		return ExchangeOperation.EXCHANGE_OPERATION.FROM_AMOUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field5() {
		return ExchangeOperation.EXCHANGE_OPERATION.TO_AMOUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Date> field6() {
		return ExchangeOperation.EXCHANGE_OPERATION.DATE;
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
		return getFromCurrencyCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getToCurrencyCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value4() {
		return getFromAmount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value5() {
		return getToAmount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date value6() {
		return getDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeOperationRecord value1(Long value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeOperationRecord value2(String value) {
		setFromCurrencyCode(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeOperationRecord value3(String value) {
		setToCurrencyCode(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeOperationRecord value4(Double value) {
		setFromAmount(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeOperationRecord value5(Double value) {
		setToAmount(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeOperationRecord value6(Date value) {
		setDate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeOperationRecord values(Long value1, String value2, String value3, Double value4, Double value5, Date value6) {
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
	 * Create a detached ExchangeOperationRecord
	 */
	public ExchangeOperationRecord() {
		super(ExchangeOperation.EXCHANGE_OPERATION);
	}

	/**
	 * Create a detached, initialised ExchangeOperationRecord
	 */
	public ExchangeOperationRecord(Long id, String fromCurrencyCode, String toCurrencyCode, Double fromAmount, Double toAmount, Date date) {
		super(ExchangeOperation.EXCHANGE_OPERATION);

		setValue(0, id);
		setValue(1, fromCurrencyCode);
		setValue(2, toCurrencyCode);
		setValue(3, fromAmount);
		setValue(4, toAmount);
		setValue(5, date);
	}
}
