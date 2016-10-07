package org.baddev.currency.fetcher;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyEntries;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyEntry;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyHistEntries;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyHistEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.baddev.currency.core.util.Safe.trySupply;

/**
 * Created by IPotapchuk on 3/15/2016.
 */
@Configuration
@ComponentScan("org.baddev.currency.fetcher")
@EnableAspectJAutoProxy
public class FetcherConfig {

    private static final Logger log = LoggerFactory.getLogger(FetcherConfig.class);

    @Value("${source_nbu}")               String nbuSourceURI;
    @Value("${enabled}")                  String proxyEnabled;
    @Value("${server}")                   String proxyHost;
    @Value("${port}")                     String proxyPort;
    @Value("${source_iso_cur}")           String isoCurSourceURI;
    @Value("${source_iso_hist}")          String isoHistSourceURI;
    @Value("${policy.receiveTimeout}")    String receiveTimeount;
    @Value("${policy.connectionTimeout}") String connectionTimeout;
    @Value("${policy.chunking}")          String chunking;
    @Value("${policy.logging.in}")        String loggingIn;
    @Value("${policy.logging.out}")       String loggingOut;

    @Bean(name = "NBUClient")
    public WebClient nbuClient() {
        WebClient client = WebClient.create(nbuSourceURI);
        configureClient(client);
        return client;
    }

    @Bean(name = "IsoCurCcys")
    public List<IsoCcyEntry> isoCurCcyEntryList() {
        IsoCcyEntries entries = trySupply(() -> {
            WebClient client = WebClient.create(isoCurSourceURI);
            configureClient(client);
            return client.accept(MediaType.TEXT_XML_TYPE).get(IsoCcyEntries.class);
        }).get();
        return (entries != null && entries.getEntries() != null) ? entries.getEntries() : new ArrayList<>();
    }

    @Bean(name = "IsoHistCcys")
    public List<IsoCcyHistEntry> isoCcyHistEntryList() {
        IsoCcyHistEntries entries = trySupply(() -> {
            WebClient client = WebClient.create(isoHistSourceURI);
            configureClient(client);
            return client.accept(MediaType.TEXT_XML_TYPE).get(IsoCcyHistEntries.class);
        }).get();
        return (entries != null && entries.getEntries() != null) ? entries.getEntries() : new ArrayList<>();
    }

    private void configureClient(WebClient client) {
        HTTPConduit conduit = (HTTPConduit) WebClient.getConfig(client).getConduit();

        HTTPClientPolicy policy = conduit.getClient();
        policy.setReceiveTimeout(Integer.parseInt(receiveTimeount));
        policy.setAllowChunking(Boolean.valueOf(chunking));
        policy.setConnectionTimeout(Integer.parseInt(connectionTimeout));

        if(Boolean.TRUE.equals(Boolean.valueOf(loggingIn))) {
            WebClient.getConfig(client).setInInterceptors(
                    Collections.singletonList(new LoggingInInterceptor())
            );
        }

        if(Boolean.TRUE.equals(Boolean.valueOf(loggingOut))){
            WebClient.getConfig(client).setOutInterceptors(
                    Collections.singletonList(new LoggingOutInterceptor())
            );
        }

        if (Boolean.TRUE.equals(Boolean.valueOf(proxyEnabled))) {
            policy.setProxyServer(proxyHost);
            policy.setProxyServerPort(Integer.parseInt(proxyPort));
            log.info("Using proxy mode: {}:{}", proxyHost, proxyPort);
        }
    }

    @Bean(name = "WebClientAspect")
    public WebClientAspect aspect() {
        return new WebClientAspect(nbuClient());
    }

}
