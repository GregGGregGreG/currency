package org.baddev.common;

import org.baddev.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Created by IPotapchuk on 10/6/2016.
 */
public class CommonErrorHandler {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private String notificationMessage;
    private String notificationTitle;
    private Severity severity;

    protected enum Severity {
        WARN, ERROR;
    }

    public final void handle(Exception e) {
        if(handleCommon(e) || handleNext(e)) {
            handled(e);
        }  else {
            handleUnexpected(e);
        }
    }

    private void handled(Exception e){
        if(!StringUtils.isEmpty(notificationMessage) && !StringUtils.isEmpty(notificationTitle) && severity != null){
            showNotification(notificationTitle, notificationMessage, severity);
        }
    }

    protected void showNotification(String title, String msg, Severity severity){
    }

    protected final void setNotificationParams(String title, String msg, Severity severity){
        this.notificationTitle = title;
        this.notificationMessage = msg;
        this.severity = severity;
    }

    protected boolean handleNext(Exception e) {
        return false;
    }

    private boolean handleCommon(Exception e) {
        if (e instanceof SQLException) {
            log.error("Error executing sql : {}; {}; {};", e.getMessage(), ((SQLException) e).getErrorCode(), ((SQLException) e).getSQLState());
            setNotificationParams("System Error", "System error occurred", Severity.ERROR);
            return true;
        }
        return false;
    }

    private void handleUnexpected(Exception e) {
        log.error("Unexpected error", e);
        setNotificationParams("Unexpected Error", "Unexpected error occurred", Severity.ERROR);
        handled(e);
    }

}
