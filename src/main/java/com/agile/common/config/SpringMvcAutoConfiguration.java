package com.agile.common.config;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.filter.CorsFilter;
import com.agile.common.filter.RequestWrapperFilter;
import com.agile.common.properties.CorsFilterProperties;
import org.jolokia.http.AgentServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServlet;

/**
 * @author 佟盟 on 2017/8/22
 */
@Configuration
public class SpringMvcAutoConfiguration implements WebMvcConfigurer {

    private final CorsFilterProperties corsFilterProperties;

    @Autowired
    public SpringMvcAutoConfiguration(CorsFilterProperties corsFilterProperties) {
        this.corsFilterProperties = corsFilterProperties;
    }

    @Bean
    @ConditionalOnClass(CorsFilter.class)
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsFilter = new FilterRegistrationBean<>();
        corsFilter.setFilter(new CorsFilter());
        corsFilter.addUrlPatterns("/*");
        corsFilter.addInitParameter("allowOrigin", corsFilterProperties.getAllowOrigin());
        corsFilter.addInitParameter("allowMethods", corsFilterProperties.getAllowMethods());
        corsFilter.addInitParameter("allowCredentials", Boolean.toString(corsFilterProperties.isAllowCredentials()));
        corsFilter.addInitParameter("allowHeaders", corsFilterProperties.getAllowHeaders());
        LoggerFactory.COMMON_LOG.debug("完成初始化跨域过滤器");
        return corsFilter;
    }

    @Bean
    @ConditionalOnClass(RequestWrapperFilter.class)
    public FilterRegistrationBean<RequestWrapperFilter> requestWrapperFilter() {
        FilterRegistrationBean<RequestWrapperFilter> requestFilter = new FilterRegistrationBean<>();
        requestFilter.setFilter(new RequestWrapperFilter());
        requestFilter.addUrlPatterns("/*");
        LoggerFactory.COMMON_LOG.debug("完成初始化Request包装过滤器");
        return requestFilter;
    }

    @Bean
    @ConditionalOnClass({AgentServlet.class})
    public ServletRegistrationBean<HttpServlet> agentServlet() {
        ServletRegistrationBean<HttpServlet> reg = new ServletRegistrationBean<>();
        reg.setServlet(new AgentServlet());
        reg.addUrlMappings("/jolokia/*");
        LoggerFactory.COMMON_LOG.debug("完成初始化Jolokia");
        return reg;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.setOrder(-1);

        //swagger
        registry.addResourceHandler("/swagger-ui.html", "/webjars/springfox-swagger-ui/*", "/webjars/springfox-swagger-ui/fonts/*")
                .addResourceLocations("/swagger-ui.html", "/webjars/springfox-swagger-ui/", "/webjars/springfox-swagger-ui/fonts/*");
    }
}
