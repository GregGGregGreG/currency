/**
 * This class is generated by jOOQ
 */
package org.baddev.currency.exchange.dao.jooq.schema;


import javax.annotation.Generated;

import org.baddev.currency.exchange.dao.jooq.schema.tables.ExchangeOperation;
import org.baddev.currency.exchange.dao.jooq.schema.tables.ExchangeRate;
import org.baddev.currency.exchange.dao.jooq.schema.tables.records.ExchangeOperationRecord;
import org.baddev.currency.exchange.dao.jooq.schema.tables.records.ExchangeRateRecord;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling foreign key relationships between tables of the <code>exchanger</code> 
 * schema
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

	// -------------------------------------------------------------------------
	// IDENTITY definitions
	// -------------------------------------------------------------------------

	public static final Identity<ExchangeOperationRecord, Long> IDENTITY_EXCHANGE_OPERATION = Identities0.IDENTITY_EXCHANGE_OPERATION;
	public static final Identity<ExchangeRateRecord, Long> IDENTITY_EXCHANGE_RATE = Identities0.IDENTITY_EXCHANGE_RATE;

	// -------------------------------------------------------------------------
	// UNIQUE and PRIMARY KEY definitions
	// -------------------------------------------------------------------------

	public static final UniqueKey<ExchangeOperationRecord> KEY_EXCHANGE_OPERATION_PRIMARY = UniqueKeys0.KEY_EXCHANGE_OPERATION_PRIMARY;
	public static final UniqueKey<ExchangeRateRecord> KEY_EXCHANGE_RATE_PRIMARY = UniqueKeys0.KEY_EXCHANGE_RATE_PRIMARY;

	// -------------------------------------------------------------------------
	// FOREIGN KEY definitions
	// -------------------------------------------------------------------------


	// -------------------------------------------------------------------------
	// [#1459] distribute members to avoid static initialisers > 64kb
	// -------------------------------------------------------------------------

	private static class Identities0 extends AbstractKeys {
		public static Identity<ExchangeOperationRecord, Long> IDENTITY_EXCHANGE_OPERATION = createIdentity(ExchangeOperation.EXCHANGE_OPERATION, ExchangeOperation.EXCHANGE_OPERATION.ID);
		public static Identity<ExchangeRateRecord, Long> IDENTITY_EXCHANGE_RATE = createIdentity(ExchangeRate.EXCHANGE_RATE, ExchangeRate.EXCHANGE_RATE.ID);
	}

	private static class UniqueKeys0 extends AbstractKeys {
		public static final UniqueKey<ExchangeOperationRecord> KEY_EXCHANGE_OPERATION_PRIMARY = createUniqueKey(ExchangeOperation.EXCHANGE_OPERATION, ExchangeOperation.EXCHANGE_OPERATION.ID);
		public static final UniqueKey<ExchangeRateRecord> KEY_EXCHANGE_RATE_PRIMARY = createUniqueKey(ExchangeRate.EXCHANGE_RATE, ExchangeRate.EXCHANGE_RATE.ID);
	}
}
