package com.agile.common.config;

import com.agile.common.annotation.ExcludeComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by 佟盟 on 2017/9/26
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@EnableCaching
@EnableScheduling
@ComponentScan(basePackages = {"com.agile.**"},excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,value = ExcludeComponentScan.class)})
public class SpringConfig {

}
