package com.agile.common.listener;

import cloud.agileframework.spring.util.spring.PropertiesUtil;
import com.agile.common.config.LoggerFactoryConfig;
import com.agile.common.container.AgileBanner;
import com.agile.common.listener.event.ListenerTest;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Properties;

/**
 * @author 佟盟
 * 日期 2019/7/29 11:34
 * 描述 工程运行监听
 * @version 1.0
 * @since 1.0
 */
public class ListenerSpringApplicationRun implements SpringApplicationRunListener {
    private SpringApplication application;
    private static Long startTime;

    public ListenerSpringApplicationRun(SpringApplication application, String[] args) {
        this.application = application;
    }

    @Override
    public void starting() {
        startTime = System.currentTimeMillis();
        application.setBanner(new AgileBanner());
        application.addListeners(new ListenerTest());
        application.setDefaultProperties(PropertiesUtil.getProperties());
        ConfigurationFactory.setConfigurationFactory(new LoggerFactoryConfig());
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        mergeAgile(environment);
        PropertiesUtil.setEnvironment(environment);
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {

    }

    @Override
    public void started(ConfigurableApplicationContext context) {

    }

    @Override
    public void running(ConfigurableApplicationContext context) {

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }

    /**
     * 1：配置变量处理
     */
    private void mergeAgile(ConfigurableEnvironment environment) {
        final String cacheName = "spring.cache.type";
        final String redisName = "redis";
        final String propertySourceName = "dynamicConfig";

        Properties properties = new Properties();
        if (redisName.equals(environment.getProperty(cacheName))) {
            properties.put(String.format("spring.jpa.properties.%s", AvailableSettings.CACHE_REGION_FACTORY), "com.agile.common.cache.redis.RedisRegionFactory");
        } else {
            properties.put(String.format("spring.jpa.properties.%s", AvailableSettings.CACHE_REGION_FACTORY), "com.agile.common.cache.ehcache.EhCacheRegionFactory");
            properties.put("spring.autoconfigure.exclude", environment.getProperty("spring.autoconfigure.exclude") + "org.springframework.boot.actuate.autoconfigure.redis.RedisHealthIndicatorAutoConfiguration");
        }

        PropertySource<?> localPropertySource = new PropertiesPropertySource(propertySourceName, properties);
        environment.getPropertySources().addLast(localPropertySource);
    }

    public static long getConsume() {
        return System.currentTimeMillis() - startTime;
    }
}
