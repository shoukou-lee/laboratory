package com.shoukou.whymytransactionalnotwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

https://sup2is.github.io/2021/11/11/about-spring-transaction.html
@EnableTransactionManagement
@Configuration
public class TxConfig {

    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        DataSource dataSource = EntityManager
        return new DataSourceTransactionManager(dataSource());
    }

    public PlatformTransactionManager transactionManager() throws URISyntaxException, GeneralSecurityException, ParseException, IOException { return new DataSourceTransactionManager(dataSource()); }


}
