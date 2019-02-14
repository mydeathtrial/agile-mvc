package com.agile.common.config;

import com.agile.common.mybatis.CustomConfiguration;
import com.agile.common.mybatis.MybatisInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 描述：
 * <p>创建时间：2018/12/14<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Configuration
@MapperScan(basePackages = {"com.agile"}, annotationClass = Mapper.class)
@ConditionalOnClass({SqlSessionFactory.class, MapperScannerConfigurer.class})
public class MyBatisConfig {
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        CustomConfiguration configuration = new CustomConfiguration();
        configuration.setCallSettersOnNulls(true);
        sessionFactory.setConfiguration(configuration);
        sessionFactory.setPlugins(new Interceptor[]{new MybatisInterceptor()});
        return sessionFactory.getObject();
    }
}
