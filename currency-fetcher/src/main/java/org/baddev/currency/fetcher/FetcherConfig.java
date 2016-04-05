package org.baddev.currency.fetcher;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by IPotapchuk on 3/15/2016.
 */
@Configuration
@ComponentScan("org.baddev.currency.fetcher")
@ImportResource("classpath:dao-applicationContext.xml")
public class FetcherConfig {

    @Bean(name = "NBUClient")
    public WebClient client(@Value("${source_nbu}") String sourceBaseURI,
                            @Value("${server}") String proxy,
                            @Value("${port}") String port) {
        WebClient client = WebClient.create(sourceBaseURI);
        HTTPConduit conduit =
                (HTTPConduit) WebClient.getConfig(client).getConduit();
        HTTPClientPolicy policy = conduit.getClient();
        policy.setProxyServer(proxy);
        policy.setProxyServerPort(Integer.parseInt(port));
        return client;
    }

}
