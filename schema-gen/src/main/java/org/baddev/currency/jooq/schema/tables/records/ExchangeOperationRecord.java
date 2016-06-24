/**
 * This class is generated by jOOQ
 */
package org.baddev.currency.jooq.schema.tables.records;


import org.baddev.currency.jooq.schema.tables.ExchangeOperation;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;


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
public class ExchangeOperationRecord extends UpdatableRecordImpl<ExchangeOperationRecord> implements Record7<Long, String, String, Double, Double, LocalDate, LocalDateTime> {

	private static final long serialVersionUID = -810782140;

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
	 * Setter for <code>exchanger.exchange_operation.from_ccy</code>.
	 */
	public void setFromCcy(String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_operation.from_ccy</code>.
	 */
	public String getFromCcy() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>exchanger.exchange_operation.to_ccy</code>.
	 */
	public void setToCcy(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_operation.to_ccy</code>.
	 */
	public String getToCcy() {
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
	 * Setter for <code>exchanger.exchange_operation.rates_date</code>.
	 */
	public void setRatesDate(LocalDate value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_operation.rates_date</code>.
	 */
	public LocalDate getRatesDate() {
		return (LocalDate) getValue(5);
	}

	/**
	 * Setter for <code>exchanger.exchange_operation.perform_datetime</code>.
	 */
	public void setPerformDatetime(LocalDateTime value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_operation.perform_datetime</code>.
	 */
	public LocalDateTime getPerformDatetime() {
		return (LocalDateTime) getValue(6);
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
	// Record7 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<Long, String, String, Double, Double, LocalDate, LocalDateTime> fieldsRow() {
		return (Row7) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<Long, String, String, Double, Double, LocalDate, LocalDateTime> valuesRow() {
		return (Row7) super.valuesRow();
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
		return ExchangeOperation.EXCHANGE_OPERATION.FROM_CCY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return ExchangeOperation.EXCHANGE_OPERATION.TO_CCY;
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
	public Field<LocalDate> field6() {
		return ExchangeOperation.EXCHANGE_OPERATION.RATES_DATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<LocalDateTime> field7() {
		return ExchangeOperation.EXCHANGE_OPERATION.PERFORM_DATETIME;
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
		return getFromCcy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getToCcy();
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
	public LocalDate value6() {
		return getRatesDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocalDateTime value7() {
		return getPerformDatetime();
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
		setFromCcy(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeOperationRecord value3(String value) {
		setToCcy(value);
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
	public ExchangeOperationRecord value6(LocalDate value) {
		setRatesDate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeOperationRecord value7(LocalDateTime value) {
		setPerformDatetime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeOperationRecord values(Long value1, String value2, String value3, Double value4, Double value5, LocalDate value6, LocalDateTime value7) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
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
	public ExchangeOperationRecord(Long id, String fromCcy, String toCcy, Double fromAmount, Double toAmount, LocalDate ratesDate, LocalDateTime performDatetime) {
		super(ExchangeOperation.EXCHANGE_OPERATION);

		setValue(0, id);
		setValue(1, fromCcy);
		setValue(2, toCcy);
		setValue(3, fromAmount);
		setValue(4, toAmount);
		setValue(5, ratesDate);
		setValue(6, performDatetime);
	}
}
