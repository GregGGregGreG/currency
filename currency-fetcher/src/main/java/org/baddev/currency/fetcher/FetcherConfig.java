package org.baddev.currency.fetcher;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.util.Collections;

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
    String sourceBaseURI;
    @Value("${enabled}")
    String proxyEnabled;
    @Value("${server}")
    String proxyHost;
    @Value("${port}")
    String proxyPort;

    @Bean(name = "NBUClient")
    public WebClient client() {
        WebClient client = WebClient.create(sourceBaseURI);
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

        return client;
    }

    @Bean(name = "WebClientAspect")
    public WebClientAspect aspect() {
        return new WebClientAspect(client());
    }

}
