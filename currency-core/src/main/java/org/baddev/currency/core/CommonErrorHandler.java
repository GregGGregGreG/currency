package org.baddev.currency.core;

import org.baddev.currency.core.exception.ServiceException;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Created by IPotapchuk on 10/6/2016.
 */
public class CommonErrorHandler {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public boolean handle(Exception e) {
        return handleCommon(e);
    }

    private boolean handleCommon(Exception e) {
        if (e instanceof SQLException) {
            log.error("Error executing sql : {}; {}; {};", e.getMessage(), ((SQLException) e).getErrorCode(), ((SQLException) e).getSQLState());
            return true;
        } else if (e instanceof DataAccessException) {
            log.error("Error accessing data : {}", e.getMessage());
            return true;
        } else if (e instanceof ServiceException) {
            log.error("Service Error: {}", e.getMessage());
            return true;
        }
        return false;
    }

}
