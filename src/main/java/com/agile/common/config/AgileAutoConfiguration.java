package com.agile.common.config;

import com.agile.common.annotation.ExcludeComponentScan;
import com.agile.common.properties.ApplicationProperties;
import com.agile.common.properties.CorsFilterProperties;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 佟盟 on 2017/9/26
 */
@Configuration
@EnableConfigurationProperties({ApplicationProperties.class, CorsFilterProperties.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@EnableCaching
@EnableScheduling
@EntityScan("com.agile.**")
@ComponentScan(basePackages = {"com.agile.**"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = ExcludeComponentScan.class)})
public class AgileAutoConfiguration {
    public AgileAutoConfiguration() {
    }
}
