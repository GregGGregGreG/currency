package org.baddev.currency.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Properties;

/**
 * Created by IPotapchuk on 10/31/2016.
 */
@Configuration
@PropertySources({
        @PropertySource("classpath:mail.properties"),
        @PropertySource("classpath:mail.pool.properties")
})
@ComponentScan("org.baddev.currency.mail")
@RequiredArgsConstructor
public class MailConfig {

    private final Environment env;

    @Bean
    ThreadPoolTaskExecutor mailerPool(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(env.getProperty("mail.pool.coreSize", Integer.class));
        executor.setKeepAliveSeconds(env.getProperty("mail.pool.keepAlive", Integer.class));
        executor.setMaxPoolSize(env.getProperty("mail.pool.maxSize", Integer.class));
        executor.setAllowCoreThreadTimeOut(env.getProperty("mail.pool.coreTimeout", Boolean.class));
        return executor;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    SimpleMailMessage templateMessage(){
        SimpleMailMessage template = new SimpleMailMessage();
        template.setFrom(env.getProperty("mail.message.from"));
        template.setReplyTo(env.getProperty("mail.replyto"));
        return template;
    }

    @Bean
    MailSender mailSender(){
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(env.getProperty("mail.host"));
        sender.setPort(env.getProperty("mail.port", Integer.class));
        sender.setUsername(env.getProperty("mail.username"));
        sender.setPassword(env.getProperty("mail.password"));

        Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", env.getProperty("mail.auth"));
        javaMailProps.put("mail.smtp.starttls.enable", env.getProperty("mail.tls.enable"));
        sender.setJavaMailProperties(javaMailProps);

        return sender;
    }

}
