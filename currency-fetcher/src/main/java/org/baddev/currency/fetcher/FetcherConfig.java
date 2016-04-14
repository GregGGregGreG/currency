package org.baddev.currency.fetcher;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.baddev.currency.fetcher.impl.nbu.entity.IsoCcyEntries;
import org.baddev.currency.fetcher.impl.nbu.entity.IsoCcyEntry;
import org.baddev.currency.fetcher.impl.nbu.entity.IsoCcyHistEntries;
import org.baddev.currency.fetcher.impl.nbu.entity.IsoCcyHistEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

/**
 * Created by IPotapchuk on 3/15/2016.
 */
@Configuration
@ComponentScan("org.baddev.currency.fetcher")
@EnableAspectJAutoProxy
@PropertySources({
        @PropertySource("classpath:api_sources.properties"),
        @PropertySource("classpath:proxy.properties")
})
public class FetcherConfig {

    private static final Logger log = LoggerFactory.getLogger(FetcherConfig.class);

    @Value("${source_nbu}")
    String nbuSourceURI;
    @Value("${enabled}")
    String proxyEnabled;
    @Value("${server}")
    String proxyHost;
    @Value("${port}")
    String proxyPort;
    @Value("${source_iso_cur}")
    String isoCurSourceURI;
    @Value("${source_iso_hist}")
    String isoHistSourceURI;

    @Bean(name = "NBUClient")
    public WebClient nbuClient() {
        WebClient client = WebClient.create(nbuSourceURI);
        configureClient(client);
        return client;
    }

    @Bean(name = "IsoCurCcys")
    public List<IsoCcyEntry> isoCurCcyEntryList() {
        WebClient client = WebClient.create(isoCurSourceURI);
        configureClient(client);
        IsoCcyEntries entries = client.accept(MediaType.TEXT_XML_TYPE).get(IsoCcyEntries.class);
        return (entries!=null && entries.getEntries()!=null)? entries.getEntries() : null;
    }

    @Bean(name = "IsoHistCcys")
    public List<IsoCcyHistEntry> isoCcyHistEntryList() {
        WebClient client = WebClient.create(isoHistSourceURI);
        configureClient(client);
        IsoCcyHistEntries entries = client.accept(MediaType.TEXT_XML_TYPE).get(IsoCcyHistEntries.class);
        return (entries!=null && entries.getEntries()!=null)? entries.getEntries() : null;
    }

    private void configureClient(WebClient client) {
        HTTPConduit conduit = (HTTPConduit) WebClient.getConfig(client).getConduit();

        HTTPClientPolicy policy = conduit.getClient();
        policy.setReceiveTimeout(8000);
        policy.setAllowChunking(false);
        policy.setConnectionTimeout(5000);

        WebClient.getConfig(client).setInInterceptors(
                Collections.singletonList(new LoggingInInterceptor())
        );

        if (proxyEnabled.equals("true")) {
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
