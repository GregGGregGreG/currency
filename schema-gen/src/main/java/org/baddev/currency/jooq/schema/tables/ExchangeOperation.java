/**
 * This class is generated by jOOQ
 */
package org.baddev.currency.jooq.schema.tables;


import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.baddev.currency.jooq.schema.Exchanger;
import org.baddev.currency.jooq.schema.Keys;
import org.baddev.currency.jooq.schema.tables.records.ExchangeOperationRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


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
public class ExchangeOperation extends TableImpl<ExchangeOperationRecord> {

	private static final long serialVersionUID = -1580002268;

	/**
	 * The reference instance of <code>exchanger.exchange_operation</code>
	 */
	public static final ExchangeOperation EXCHANGE_OPERATION = new ExchangeOperation();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ExchangeOperationRecord> getRecordType() {
		return ExchangeOperationRecord.class;
	}

	/**
	 * The column <code>exchanger.exchange_operation.id</code>.
	 */
	public final TableField<ExchangeOperationRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>exchanger.exchange_operation.from_currency_code</code>.
	 */
	public final TableField<ExchangeOperationRecord, String> FROM_CURRENCY_CODE = createField("from_currency_code", org.jooq.impl.SQLDataType.VARCHAR.length(3).nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>exchanger.exchange_operation.to_currency_code</code>.
	 */
	public final TableField<ExchangeOperationRecord, String> TO_CURRENCY_CODE = createField("to_currency_code", org.jooq.impl.SQLDataType.VARCHAR.length(3).nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>exchanger.exchange_operation.from_amount</code>.
	 */
	public final TableField<ExchangeOperationRecord, Double> FROM_AMOUNT = createField("from_amount", org.jooq.impl.SQLDataType.DOUBLE.nullable(false), this, "");

	/**
	 * The column <code>exchanger.exchange_operation.to_amount</code>.
	 */
	public final TableField<ExchangeOperationRecord, Double> TO_AMOUNT = createField("to_amount", org.jooq.impl.SQLDataType.DOUBLE.nullable(false), this, "");

	/**
	 * The column <code>exchanger.exchange_operation.date</code>.
	 */
	public final TableField<ExchangeOperationRecord, Date> DATE = createField("date", org.jooq.impl.SQLDataType.DATE.nullable(false), this, "");

	/**
	 * Create a <code>exchanger.exchange_operation</code> table reference
	 */
	public ExchangeOperation() {
		this("exchange_operation", null);
	}

	/**
	 * Create an aliased <code>exchanger.exchange_operation</code> table reference
	 */
	public ExchangeOperation(String alias) {
		this(alias, EXCHANGE_OPERATION);
	}

	private ExchangeOperation(String alias, Table<ExchangeOperationRecord> aliased) {
		this(alias, aliased, null);
	}

	private ExchangeOperation(String alias, Table<ExchangeOperationRecord> aliased, Field<?>[] parameters) {
		super(alias, Exchanger.EXCHANGER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ExchangeOperationRecord> getPrimaryKey() {
		return Keys.KEY_EXCHANGE_OPERATION_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ExchangeOperationRecord>> getKeys() {
		return Arrays.<UniqueKey<ExchangeOperationRecord>>asList(Keys.KEY_EXCHANGE_OPERATION_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExchangeOperation as(String alias) {
		return new ExchangeOperation(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ExchangeOperation rename(String name) {
		return new ExchangeOperation(name, null);
	}
}
