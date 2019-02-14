package com.agile.common.config;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.filter.CORSFilter;
import com.agile.common.properties.ServletProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

/**
 * @author 佟盟 on 2017/8/22
 */
@Configuration
public class SpringMvcAutoConfiguration implements WebMvcConfigurer {

    private final ServletProperties servletProperties;

    @Autowired
    public SpringMvcAutoConfiguration(ServletProperties servletProperties) {
        this.servletProperties = servletProperties;
    }

    @Bean
    @ConditionalOnClass(CORSFilter.class)
    public FilterRegistrationBean corsFilter() {
        if (LoggerFactory.COMMON_LOG.isDebugEnabled()) {
            LoggerFactory.COMMON_LOG.debug("完成初始化跨域过滤器");
        }

        FilterRegistrationBean<Filter> encodingFilter = new FilterRegistrationBean<>();
        encodingFilter.setFilter(new CORSFilter());
        encodingFilter.addUrlPatterns("/*");
        encodingFilter.addInitParameter("allowOrigin", servletProperties.getAllowOrigin());
        encodingFilter.addInitParameter("allowMethods", servletProperties.getAllowMethods());
        encodingFilter.addInitParameter("allowCredentials", Boolean.toString(servletProperties.isAllowCredentials()));
        encodingFilter.addInitParameter("allowHeaders", servletProperties.getAllowHeaders());
        return encodingFilter;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.setOrder(-1);

        //swagger
        registry.addResourceHandler("/swagger-ui.html", "/webjars/springfox-swagger-ui/*", "/webjars/springfox-swagger-ui/fonts/*")
                .addResourceLocations("/swagger-ui.html", "/webjars/springfox-swagger-ui/", "/webjars/springfox-swagger-ui/fonts/*");
    }
}
