package org.baddev.currency.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Created by IPotapchuk on 10/31/2016.
 */
@Configuration
@PropertySource("classpath:scheduler.pool.properties")
@ComponentScan("org.baddev.currency.scheduler")
@RequiredArgsConstructor
public class SchedulerConfig {

    private final Environment env;

    @Bean
    ThreadPoolTaskScheduler scheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(env.getProperty("scheduler.pool.poolSize", Integer.class));
        scheduler.setRemoveOnCancelPolicy(env.getProperty("scheduler.pool.removeOnCancelPolicy", Boolean.class));
        scheduler.setWaitForTasksToCompleteOnShutdown(env.getProperty("scheduler.pool.waitForTasksToCompleteOnShutdown", Boolean.class));
        return scheduler;
    }
}
