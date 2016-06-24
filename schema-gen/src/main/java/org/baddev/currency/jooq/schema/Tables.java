/**
 * This class is generated by jOOQ
 */
package org.baddev.currency.jooq.schema;


import org.baddev.currency.jooq.schema.tables.ExchangeOperation;
import org.baddev.currency.jooq.schema.tables.ExchangeRate;
import org.baddev.currency.jooq.schema.tables.ExchangeTask;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in exchanger
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

	/**
	 * The table exchanger.exchange_operation
	 */
	public static final ExchangeOperation EXCHANGE_OPERATION = org.baddev.currency.jooq.schema.tables.ExchangeOperation.EXCHANGE_OPERATION;

	/**
	 * The table exchanger.exchange_rate
	 */
	public static final ExchangeRate EXCHANGE_RATE = org.baddev.currency.jooq.schema.tables.ExchangeRate.EXCHANGE_RATE;

	/**
	 * The table exchanger.exchange_task
	 */
	public static final ExchangeTask EXCHANGE_TASK = org.baddev.currency.jooq.schema.tables.ExchangeTask.EXCHANGE_TASK;
}
