package com.agile.common.config;

import com.agile.common.mvc.model.dao.Dao;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 佟盟 on 2017/10/7
 */
@ConditionalOnClass(DruidDataSource.class)
@Configuration
public class DruidAutoConfiguration {
    @Bean
    public Dao dao() {
        return new Dao();
    }
}
