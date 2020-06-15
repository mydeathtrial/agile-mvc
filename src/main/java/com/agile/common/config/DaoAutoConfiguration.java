package com.agile.common.config;

import com.agile.common.filter.DruidFilter;
import com.agile.common.mvc.model.dao.Dao;
import com.alibaba.druid.filter.Filter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author 佟盟 on 2017/10/7
 */
@Configuration
@ConditionalOnClass(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class DaoAutoConfiguration {

    @Bean
    @ConfigurationProperties("agile.dao")
    @ConditionalOnProperty(prefix = "agile.dao", name = "enable")
    @ConditionalOnMissingBean
    public Dao dao() {
        return new Dao();
    }

    @ConfigurationProperties("spring.datasource.druid.filter.print")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.filter.print", name = "enabled")
    @ConditionalOnMissingBean
    @Bean
    public Filter druidFilter() {
        return new DruidFilter();
    }
}
