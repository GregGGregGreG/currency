/**
 * This class is generated by jOOQ
 */
package org.baddev.currency.jooq.schema.tables.records;


import org.baddev.currency.jooq.schema.tables.ExchangeTask;
import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.TableRecordImpl;

import javax.annotation.Generated;
import java.sql.Date;


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
public class ExchangeTaskRecord extends TableRecordImpl<ExchangeTaskRecord> implements Record7<Long, Date, String, String, Double, String, Byte> {

	private static final long serialVersionUID = 2114530679;

	/**
	 * Setter for <code>exchanger.exchange_task.id</code>.
	 */
	public void setId(Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_task.id</code>.
	 */
	public Long getId() {
		return (Long) getValue(0);
	}

	/**
	 * Setter for <code>exchanger.exchange_task.date_added</code>.
	 */
	public void setDateAdded(Date value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_task.date_added</code>.
	 */
	public Date getDateAdded() {
		return (Date) getValue(1);
	}

	/**
	 * Setter for <code>exchanger.exchange_task.from_ccy</code>.
	 */
	public void setFromCcy(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_task.from_ccy</code>.
	 */
	public String getFromCcy() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>exchanger.exchange_task.to_ccy</code>.
	 */
	public void setToCcy(String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_task.to_ccy</code>.
	 */
	public String getToCcy() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>exchanger.exchange_task.amount</code>.
	 */
	public void setAmount(Double value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_task.amount</code>.
	 */
	public Double getAmount() {
		return (Double) getValue(4);
	}

	/**
	 * Setter for <code>exchanger.exchange_task.cron</code>.
	 */
	public void setCron(String value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_task.cron</code>.
	 */
	public String getCron() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>exchanger.exchange_task.active</code>.
	 */
	public void setActive(Byte value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>exchanger.exchange_task.active</code>.
	 */
	public Byte getActive() {
		return (Byte) getValue(6);
	}

	// -------------------------------------------------------------------------
	// Record7 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<Long, Date, String, String, Double, String, Byte> fieldsRow() {
		return (Row7) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<Long, Date, String, String, Double, String, Byte> valuesRow() {
		return (Row7) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Long> field1() {
		return ExchangeTask.EXCHANGE_TASK.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Date> field2() {
		return ExchangeTask.EXCHANGE_TASK.DATE_ADDED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return ExchangeTask.EXCHANGE_TASK.FROM_CCY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return ExchangeTask.EXCHANGE_TASK.TO_CCY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field5() {
		return ExchangeTask.EXCHANGE_TASK.AMOUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return ExchangeTask.EXCHANGE_TASK.CRON;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Byte> field7() {
		return ExchangeTask.EXCHANGE_TASK.ACTIVE;
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
	public Date value2() {
		return getDateAdded();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getFromCcy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getToCcy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value5() {
		return getAmount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getCron();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte value7() {
		return getActive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeTaskRecord value1(Long value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeTaskRecord value2(Date value) {
		setDateAdded(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeTaskRecord value3(String value) {
		setFromCcy(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeTaskRecord value4(String value) {
		setToCcy(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeTaskRecord value5(Double value) {
		setAmount(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeTaskRecord value6(String value) {
		setCron(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeTaskRecord value7(Byte value) {
		setActive(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeTaskRecord values(Long value1, Date value2, String value3, String value4, Double value5, String value6, Byte value7) {
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
	 * Create a detached ExchangeTaskRecord
	 */
	public ExchangeTaskRecord() {
		super(ExchangeTask.EXCHANGE_TASK);
	}

	/**
	 * Create a detached, initialised ExchangeTaskRecord
	 */
	public ExchangeTaskRecord(Long id, Date dateAdded, String fromCcy, String toCcy, Double amount, String cron, Byte active) {
		super(ExchangeTask.EXCHANGE_TASK);

		setValue(0, id);
		setValue(1, dateAdded);
		setValue(2, fromCcy);
		setValue(3, toCcy);
		setValue(4, amount);
		setValue(5, cron);
		setValue(6, active);
	}
}