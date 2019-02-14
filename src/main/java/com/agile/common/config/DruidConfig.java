package com.agile.common.config;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.filter.DruidFilter;
import com.agile.common.properties.DruidConfigProperties;
import com.agile.common.util.DataBaseUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServlet;
import java.sql.SQLException;
import java.util.Collections;

/**
 * @author 佟盟 on 2017/10/7
 */
@EnableConfigurationProperties(value = {DruidConfigProperties.class})
@Configuration
public class DruidConfig {
    @Autowired
    private DruidConfigProperties druidConfigProperty;

    @Bean
    public ServletRegistrationBean druidServlet() {
        if (LoggerFactory.COMMON_LOG.isDebugEnabled()) {
            LoggerFactory.COMMON_LOG.debug("完成初始化Druid连接池仪表盘");
        }

        ServletRegistrationBean<HttpServlet> reg = new ServletRegistrationBean<>();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings(druidConfigProperty.getUrl() + "/*");
        reg.addInitParameter("loginUsername", druidConfigProperty.getManagerName());
        reg.addInitParameter("loginPassword", druidConfigProperty.getManagerPassword());
        reg.addInitParameter("resetEnable", Boolean.toString(druidConfigProperty.isResetEnable()));
        return reg;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        if (LoggerFactory.COMMON_LOG.isDebugEnabled()) {
            LoggerFactory.COMMON_LOG.debug("完成初始化Druid监控过滤器");
        }

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", druidConfigProperty.getExclusions());
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        filterRegistrationBean.addInitParameter("sessionStatMaxCount", Long.toString(druidConfigProperty.getSessionStatMaxCount()));
        return filterRegistrationBean;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    DruidDataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();

        DataBaseUtil.DB db = druidConfigProperty.getType();
        druidDataSource.setDriverClassName(db.getDriver());
        druidDataSource.setValidationQuery(db.getTestSql());
        druidDataSource.setUrl(DataBaseUtil.createDBUrl(new DataBaseUtil.DBInfo(druidConfigProperty)));

        druidDataSource.setUsername(druidConfigProperty.getDataBaseUsername());
        druidDataSource.setPassword(druidConfigProperty.getDataBasePassword());
        druidDataSource.setInitialSize(druidConfigProperty.getInitSize());
        druidDataSource.setMinIdle(druidConfigProperty.getMinIdle());
        druidDataSource.setMaxActive(druidConfigProperty.getMaxActive());
        druidDataSource.setMaxWait(druidConfigProperty.getMaxWait());
        druidDataSource.setRemoveAbandoned(druidConfigProperty.isRemoveAbandoned());
        druidDataSource.setRemoveAbandonedTimeout(druidConfigProperty.getRemoveAbandonedTimeout());
        druidDataSource.setTimeBetweenEvictionRunsMillis(druidConfigProperty.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(druidConfigProperty.getMinEvictableIdleTimeMillis());

        druidDataSource.setTestWhileIdle(druidConfigProperty.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(druidConfigProperty.isTestOnBorrow());
        druidDataSource.setTestOnReturn(druidConfigProperty.isTestOnReturn());
        druidDataSource.setFilters(druidConfigProperty.getFilters());
        druidDataSource.setPoolPreparedStatements(druidConfigProperty.isPoolPreparedStatements());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(druidConfigProperty.getMaxPoolPreparedStatementPerConnectionSize());
        druidDataSource.setUseGlobalDataSourceStat(druidConfigProperty.isGlobalDataSourceStat());

        druidDataSource.setProxyFilters(Collections.singletonList(new DruidFilter()));
        return druidDataSource;

    }
}
