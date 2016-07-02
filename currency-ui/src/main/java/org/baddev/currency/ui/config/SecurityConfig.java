package org.baddev.currency.ui.config;

import org.baddev.currency.security.UserDetailsServiceImpl;
import org.baddev.currency.ui.security.VaadinSessionSecurityContextHolderStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends GlobalMethodSecurityConfiguration {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService())
                .passwordEncoder(new Md5PasswordEncoder());
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    AuthenticationManager authManager() throws Exception {
        return authenticationManager();
    }

    static {
        SecurityContextHolder.setStrategyName(VaadinSessionSecurityContextHolderStrategy.class.getName());
    }
}