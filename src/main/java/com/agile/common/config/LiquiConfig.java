package com.agile.common.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by 佟盟 on 2018/11/21
 */
@Configuration
public class LiquiConfig {
    @Bean
    public SpringLiquibase springLiquibase(DataSource dataSource){
        SpringLiquibase liquibase=new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:com/agile/conf/master.xml");
        liquibase.setShouldRun(true);
        liquibase.setDropFirst(false);
        return liquibase;
    }
}
