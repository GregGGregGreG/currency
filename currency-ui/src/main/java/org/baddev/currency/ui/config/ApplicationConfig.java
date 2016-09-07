package org.baddev.currency.ui.config;

import com.google.common.eventbus.EventBus;
import com.vaadin.spring.annotation.EnableVaadin;
import org.baddev.currency.fetcher.FetcherConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

@Configuration
@EnableVaadin
@Import(FetcherConfig.class)
@ImportResource(locations = {
        "classpath:exchanger-applicationContext.xml",
        "classpath:scheduler-applicationContext.xml",
        "classpath:mail-applicationContext.xml"
})
public class ApplicationConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    EventBus bus() {
        return new EventBus();
    }

}