/**
 * This class is generated by jOOQ
 */
package org.baddev.currency.jooq.schema;


import org.baddev.currency.jooq.schema.tables.*;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in exchanger
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>exchanger.exchange_operation</code>.
     */
    public static final ExchangeOperation EXCHANGE_OPERATION = org.baddev.currency.jooq.schema.tables.ExchangeOperation.EXCHANGE_OPERATION;

    /**
     * The table <code>exchanger.exchange_rate</code>.
     */
    public static final ExchangeRate EXCHANGE_RATE = org.baddev.currency.jooq.schema.tables.ExchangeRate.EXCHANGE_RATE;

    /**
     * The table <code>exchanger.exchange_task</code>.
     */
    public static final ExchangeTask EXCHANGE_TASK = org.baddev.currency.jooq.schema.tables.ExchangeTask.EXCHANGE_TASK;

    /**
     * The table <code>exchanger.user</code>.
     */
    public static final User USER = org.baddev.currency.jooq.schema.tables.User.USER;

    /**
     * The table <code>exchanger.user_details</code>.
     */
    public static final UserDetails USER_DETAILS = org.baddev.currency.jooq.schema.tables.UserDetails.USER_DETAILS;

    /**
     * The table <code>exchanger.user_role</code>.
     */
    public static final UserRole USER_ROLE = org.baddev.currency.jooq.schema.tables.UserRole.USER_ROLE;

    /**
     * The table <code>exchanger.user_user_role</code>.
     */
    public static final UserUserRole USER_USER_ROLE = org.baddev.currency.jooq.schema.tables.UserUserRole.USER_USER_ROLE;
}
