package com.agile.common.config;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.filter.DruidFilter;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.properties.DruidConfigProperties;
import com.agile.common.util.DataBaseUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.util.Collections;

/**
 * @author 佟盟 on 2017/10/7
 */
@EnableConfigurationProperties(value = {DruidConfigProperties.class})
@ConditionalOnClass(DruidDataSource.class)
@AutoConfigureBefore({DataSourceAutoConfiguration.class, JpaBaseConfiguration.class})
@Configuration
public class DruidAutoConfiguration {
    private final DruidConfigProperties druidConfigProperty;
    private static final String LOGIN_USERNAME = "loginUsername";
    private static final String LOGIN_PASSWORD = "loginPassword";
    private static final String RESET_ENABLE = "resetEnable";
    private static final String EXCLUSIONS = "exclusions";
    private static final String PROFILE_ENABLE = "profileEnable";
    private static final String SESSION_STAT_MAX_COUNT = "sessionStatMaxCount";

    @Autowired
    public DruidAutoConfiguration(DruidConfigProperties druidConfigProperty) {
        this.druidConfigProperty = druidConfigProperty;
        druidConfigProperty.setProxyFilters(Collections.singletonList(new DruidFilter()));
        druidConfigProperty.setUrl(DataBaseUtil.createDBUrl(new DataBaseUtil.DBInfo(druidConfigProperty)));
    }

    @Bean
    @ConditionalOnClass({StatViewServlet.class})
    public ServletRegistrationBean druidServlet() {
        if (LoggerFactory.COMMON_LOG.isDebugEnabled()) {
            LoggerFactory.COMMON_LOG.debug("完成初始化Druid连接池仪表盘");
        }

        ServletRegistrationBean<HttpServlet> reg = new ServletRegistrationBean<>();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings(druidConfigProperty.getDashboardUrl() + "/*");
        reg.addInitParameter(LOGIN_USERNAME, druidConfigProperty.getManagerName());
        reg.addInitParameter(LOGIN_PASSWORD, druidConfigProperty.getManagerPassword());
        reg.addInitParameter(RESET_ENABLE, Boolean.toString(druidConfigProperty.isResetEnable()));
        return reg;
    }

    @Bean
    @ConditionalOnClass({WebStatFilter.class})
    public FilterRegistrationBean filterRegistrationBean() {
        if (LoggerFactory.COMMON_LOG.isDebugEnabled()) {
            LoggerFactory.COMMON_LOG.debug("完成初始化Druid监控过滤器");
        }

        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter(EXCLUSIONS, druidConfigProperty.getExclusions());
        filterRegistrationBean.addInitParameter(PROFILE_ENABLE, "true");
        filterRegistrationBean.addInitParameter(SESSION_STAT_MAX_COUNT, Long.toString(druidConfigProperty.getSessionStatMaxCount()));
        return filterRegistrationBean;
    }

    @Bean
    public Dao dao() {
        return new Dao();
    }
}
