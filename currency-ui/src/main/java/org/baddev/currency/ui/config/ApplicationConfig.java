package org.baddev.currency.ui.config;

import com.google.common.eventbus.EventBus;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableVaadin
public class ApplicationConfig {

    @Bean
    @UIScope
    EventBus bus() {
        return new EventBus();
    }

}