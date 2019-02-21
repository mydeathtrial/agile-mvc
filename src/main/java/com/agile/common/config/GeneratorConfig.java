package com.agile.common.config;

import com.agile.common.annotation.ExcludeComponentScan;
import com.agile.common.properties.DruidConfigProperties;
import com.agile.common.properties.GeneratorProperties;
import com.agile.common.properties.LoggerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/2/11 13:52
 * @Description TODO
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(value = {DruidConfigProperties.class, GeneratorProperties.class, LoggerProperties.class})
@ExcludeComponentScan
@ComponentScan(basePackages = {"com.agile.common.container", "com.agile.common.properties"})
public class GeneratorConfig {

}