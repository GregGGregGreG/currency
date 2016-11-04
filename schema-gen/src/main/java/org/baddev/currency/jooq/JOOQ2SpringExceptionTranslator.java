package org.baddev.currency.jooq;

import org.jooq.ExecuteContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultExecuteListener;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

/**
 * Created by IPotapchuk on 2/15/2016.
 */
class JOOQ2SpringExceptionTranslator extends DefaultExecuteListener {

    private static final long serialVersionUID = 6619729023075572982L;

    @Override
    public void exception(ExecuteContext ctx) {
        SQLDialect dialect = ctx.configuration().dialect();
        SQLExceptionTranslator translator = (dialect != null)
                ? new SQLErrorCodeSQLExceptionTranslator(dialect.name())
                : new SQLStateSQLExceptionTranslator();
        ctx.exception(translator.translate("jOOQ", ctx.sql(), ctx.sqlException()));
    }

}
