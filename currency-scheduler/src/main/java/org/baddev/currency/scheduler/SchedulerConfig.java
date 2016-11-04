package org.baddev.currency.scheduler;

import org.baddev.currency.core.CommonErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.TaskUtils;

/**
 * Created by IPotapchuk on 10/31/2016.
 */
@Configuration
@PropertySource("classpath:scheduler.pool.properties")
@ComponentScan("org.baddev.currency.scheduler")
public class SchedulerConfig {

    @Autowired
    private Environment env;

    @Bean
    ThreadPoolTaskScheduler scheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setErrorHandler(t -> {
            if (t instanceof Exception)
                new CommonErrorHandler().handle((Exception) t);
            else TaskUtils.getDefaultErrorHandler(false).handleError(t);
        });
        scheduler.setPoolSize(env.getProperty("scheduler.pool.poolSize", Integer.class));
        scheduler.setRemoveOnCancelPolicy(env.getProperty("scheduler.pool.removeOnCancelPolicy", Boolean.class));
        scheduler.setWaitForTasksToCompleteOnShutdown(env.getProperty("scheduler.pool.waitForTasksToCompleteOnShutdown", Boolean.class));
        return scheduler;
    }

}
