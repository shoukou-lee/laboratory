//package com.shoukou.whymytransactionalnotwork.config;
//
//import com.zaxxer.hikari.HikariDataSource;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
//@EnableTransactionManagement
//@Configuration
//public class TxConfig {
//
//    private final String SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/test";
//    private final String SPRING_DATASOURCE_USERNAME = "test";
//    private final String SPRING_DATASOURCE_PASSWORD = "test";
//    private final String SPRING_DATASOURCE_DRIVER_CLASS_NAME = "test";
//
//    @Bean
//    public DataSource dataSource() {
//        HikariDataSource hikariDataSource = new HikariDataSource();
//        hikariDataSource.setJdbcUrl(SPRING_DATASOURCE_USERNAME);
//        hikariDataSource.setUsername(SPRING_DATASOURCE_USERNAME);
//        hikariDataSource.setPassword(SPRING_DATASOURCE_PASSWORD);
//        hikariDataSource.setDriverClassName(SPRING_DATASOURCE_DRIVER_CLASS_NAME);
//
//        return hikariDataSource;
//    }
//
//    @Bean
//    public PlatformTransactionManager platformTransactionManager() {
//        return new DataSourceTransactionManager(dataSource());
//    }
//
//}
