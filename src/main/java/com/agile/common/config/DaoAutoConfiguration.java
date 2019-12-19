package com.agile.common.config;

import com.agile.common.filter.DruidFilter;
import com.agile.common.mvc.model.dao.Dao;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collections;

/**
 * @author 佟盟 on 2017/10/7
 */
@Configuration
@AutoConfigureAfter(DruidDataSourceAutoConfigure.class)
public class DaoAutoConfiguration {
    @Autowired
    public DaoAutoConfiguration(DruidDataSource dataSource) {
        dataSource.setProxyFilters(Collections.singletonList(new DruidFilter()));
    }

    @ConditionalOnClass(DataSource.class)
    @Bean
    public Dao dao() {
        return new Dao();
    }

}
