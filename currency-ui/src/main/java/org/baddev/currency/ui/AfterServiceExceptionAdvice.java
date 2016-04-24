package org.baddev.currency.ui;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Notification;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.baddev.currency.core.ServiceException;

/**
 * Created by Ilya on 24.04.2016.
 */
@Aspect
public class AfterServiceExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(AfterServiceExceptionAdvice.class);

    @AfterThrowing(pointcut = "execution(* org.baddev.currency.fetcher.other.Iso4217CcyService.*(..))",
            throwing = "ex")
    public void processException(ServiceException ex) {
        log.info("Works");
        Notification.show(ex.getMessage(), Notification.Type.ERROR_MESSAGE);
    }

}
