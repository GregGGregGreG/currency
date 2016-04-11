package org.baddev.currency.fetcher;

import org.apache.cxf.jaxrs.client.WebClient;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ilya on 11.04.2016.
 */
@Aspect
public class WebClientAspect {

    private static final Logger log = LoggerFactory.getLogger(WebClientAspect.class);

    private WebClient client;

    public WebClientAspect() {
    }

    public WebClientAspect(WebClient client) {
        this.client = client;
    }

    @After("execution(* org.baddev.currency.core.fetcher.ExchangeRateFetcher.*(..))")
    public void reset(){
        log.info("Going to reset client");
        client.reset();
    }

}
