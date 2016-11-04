package org.baddev.currency.jooq;

import org.apache.commons.dbcp.BasicDataSource;
import org.baddev.currency.jooq.transaction.SpringTransactionProvider;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.TransactionProvider;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Created by IPotapchuk on 10/31/2016.
 */
@Configuration
@ComponentScan("org.baddev.currency.jooq")
@PropertySource("classpath:db.properties")
@EnableTransactionManagement
public class DataAccessConfig {

    @Autowired
    private Environment env;

    @Bean
    DataSource dataSource(){
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(env.getProperty("db.driver"));
        ds.setUrl(env.getProperty("db.url"));
        ds.setUsername(env.getProperty("db.user"));
        ds.setPassword(env.getProperty("db.password"));
        return ds;
    }

    @Bean
    DataSourceTransactionManager txManager(DataSource dataSource){
        DataSourceTransactionManager manager = new DataSourceTransactionManager();
        manager.setDataSource(dataSource);
        return manager;
    }

    @Bean
    TransactionProvider txProvider(DataSourceTransactionManager manager){
        SpringTransactionProvider provider = new SpringTransactionProvider();
        provider.setTxMgr(manager);
        return provider;
    }

    @Bean
    TransactionAwareDataSourceProxy txAwareDSProxy(DataSource dataSource){
        return new TransactionAwareDataSourceProxy(dataSource);
    }

    @Bean
    DataSourceConnectionProvider connectionProvider(TransactionAwareDataSourceProxy proxy) {
        return new DataSourceConnectionProvider(proxy);
    }

    @Bean
    org.jooq.Configuration config(ConnectionProvider cnProvider,
                                  TransactionProvider txProvider){
        DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.setSQLDialect(SQLDialect.MYSQL);
        configuration.setConnectionProvider(cnProvider);
        configuration.setTransactionProvider(txProvider);
        configuration.setExecuteListenerProvider(
                new DefaultExecuteListenerProvider(new JOOQ2SpringExceptionTranslator()));
        return configuration;
    }

    @Bean
    DSLContext dslContext(org.jooq.Configuration configuration){
        return new DefaultDSLContext(configuration);
    }

}
