/**
 * This class is generated by jOOQ
 */
package org.baddev.currency.jooq.schema.tables;


import org.baddev.currency.jooq.schema.Exchanger;
import org.baddev.currency.jooq.schema.Keys;
import org.baddev.currency.jooq.schema.tables.records.UserDetailsRecord;
import org.jooq.*;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.util.Arrays;
import java.util.List;


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
public class UserDetails extends TableImpl<UserDetailsRecord> {

    private static final long serialVersionUID = 1694593291;

    /**
     * The reference instance of <code>exchanger.user_details</code>
     */
    public static final UserDetails USER_DETAILS = new UserDetails();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserDetailsRecord> getRecordType() {
        return UserDetailsRecord.class;
    }

    /**
     * The column <code>exchanger.user_details.user_id</code>.
     */
    public final TableField<UserDetailsRecord, Long> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>exchanger.user_details.first_name</code>.
     */
    public final TableField<UserDetailsRecord, String> FIRST_NAME = createField("first_name", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

    /**
     * The column <code>exchanger.user_details.last_name</code>.
     */
    public final TableField<UserDetailsRecord, String> LAST_NAME = createField("last_name", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

    /**
     * Create a <code>exchanger.user_details</code> table reference
     */
    public UserDetails() {
        this("user_details", null);
    }

    /**
     * Create an aliased <code>exchanger.user_details</code> table reference
     */
    public UserDetails(String alias) {
        this(alias, USER_DETAILS);
    }

    private UserDetails(String alias, Table<UserDetailsRecord> aliased) {
        this(alias, aliased, null);
    }

    private UserDetails(String alias, Table<UserDetailsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Exchanger.EXCHANGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<UserDetailsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<UserDetailsRecord, ?>>asList(Keys.USER_DETAILS_USER_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails as(String alias) {
        return new UserDetails(alias, this);
    }

    /**
     * Rename this table
     */
    public UserDetails rename(String name) {
        return new UserDetails(name, null);
    }
}
