package org.baddev.currency.fetcher;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.baddev.currency.core.meta.Prod;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyEntries;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyEntry;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyHistEntries;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyHistEntry;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.baddev.common.utils.Safe.trySupply;

/**
 * Created by IPotapchuk on 3/15/2016.
 */
@Configuration
@ComponentScan("org.baddev.currency.fetcher")
@PropertySources({
        @PropertySource("classpath:api_sources.properties"),
        @PropertySource("classpath:fetcher_policy.properties"),
        @PropertySource("classpath:proxy.properties")
})
public class FetcherConfig {

    @Autowired private Logger      log;
    @Autowired private Environment env;

    @Bean(name = "NBUClient")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebClient nbuClient() {
        WebClient client = WebClient.create(env.getProperty("nbu.url"));
        configureClient(client);
        return client;
    }

    @Bean(name = "IsoCurCcys")
    @Prod
    public List<IsoCcyEntry> isoCurCcyEntryList() {
        return Collections.unmodifiableList(
                trySupply(() -> {
                    WebClient client = WebClient.create(env.getProperty("iso4217.url.cur"));
                    configureClient(client);
                    return client.accept(MediaType.TEXT_XML_TYPE).get(IsoCcyEntries.class);
                }).map(IsoCcyEntries::getEntries).orElse(new ArrayList<>())
        );
    }

    @Bean(name = "IsoHistCcys")
    @Prod
    public List<IsoCcyHistEntry> isoCcyHistEntryList() {
        return Collections.unmodifiableList(
                trySupply(() -> {
                    WebClient client = WebClient.create(env.getProperty("iso4217.url.hist"));
                    configureClient(client);
                    return client.accept(MediaType.TEXT_XML_TYPE).get(IsoCcyHistEntries.class);
                }).map(IsoCcyHistEntries::getEntries).orElse(new ArrayList<>())
        );
    }

    private void configureClient(WebClient client) {
        HTTPConduit conduit = (HTTPConduit) WebClient.getConfig(client).getConduit();

        HTTPClientPolicy policy = conduit.getClient();
        policy.setReceiveTimeout(env.getProperty("policy.receiveTimeout", Integer.class));
        policy.setAllowChunking(env.getProperty("policy.chunking", Boolean.class));
        policy.setConnectionTimeout(env.getProperty("policy.connectionTimeout", Integer.class));

        if(Boolean.TRUE.equals(env.getProperty("policy.logging.in", Boolean.class))) {
            WebClient.getConfig(client).setInInterceptors(
                    Collections.singletonList(new LoggingInInterceptor())
            );
        }

        if(Boolean.TRUE.equals(env.getProperty("policy.logging.out", Boolean.class))){
            WebClient.getConfig(client).setOutInterceptors(
                    Collections.singletonList(new LoggingOutInterceptor())
            );
        }

        if (Boolean.TRUE.equals(env.getProperty("proxy.enabled", Boolean.class))) {
            policy.setProxyServer(env.getProperty("proxy.host"));
            policy.setProxyServerPort(env.getProperty("proxy.port", Integer.class));
            log.info("Using proxy mode: {}:{}", env.getProperty("proxy.host"), env.getProperty("proxy.port"));
        }
    }

}
