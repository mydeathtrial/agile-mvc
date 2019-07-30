package com.agile.common.listener;

import com.agile.common.config.LoggerFactoryConfig;
import com.agile.common.container.AgileBanner;
import com.agile.common.listener.event.ListenerTest;
import com.agile.common.util.PropertiesUtil;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.time.Duration;

import static org.springframework.context.support.PropertySourcesPlaceholderConfigurer.LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME;

/**
 * @author 佟盟
 * 日期 2019/7/29 11:34
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class ListenerSpringApplicationRun implements SpringApplicationRunListener {
    private long startTime;
    private SpringApplication application;

    public ListenerSpringApplicationRun(SpringApplication application, String[] args) {
        startTime = System.currentTimeMillis();
        this.application = application;
    }

    @Override
    public void starting() {
        application.setBanner(new AgileBanner());
        application.addListeners(new ListenerTest());
        application.setDefaultProperties(PropertiesUtil.getProperties());
        ConfigurationFactory.setConfigurationFactory(new LoggerFactoryConfig());
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        mergeAgile(environment);
    }

    /**
     * 1：配置变量处理
     */
    private void mergeAgile(ConfigurableEnvironment environment) {
        final String cacheName = "spring.cache.type";
        final String redisName = "redis";
        if (redisName.equals(PropertiesUtil.getProperty(cacheName))) {
            PropertiesUtil.setProperties(String.format("spring.jpa.properties.%s", AvailableSettings.CACHE_REGION_FACTORY), "com.agile.common.cache.redis.RedisRegionFactory");
        } else {
            PropertiesUtil.setProperties(String.format("spring.jpa.properties.%s", AvailableSettings.CACHE_REGION_FACTORY), "com.agile.common.cache.ehcache.EhCacheRegionFactory");
            PropertiesUtil.appendProperties("spring.autoconfigure.exclude", "org.springframework.boot.actuate.autoconfigure.redis.RedisHealthIndicatorAutoConfiguration");
        }
        PropertySource<?> localPropertySource = new PropertiesPropertySource(LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME, PropertiesUtil.getProperties());
        environment.getPropertySources().addLast(localPropertySource);
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
        print(STATUS.RUNNING);
        System.gc();
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        print(STATUS.ERROR);
    }

    void print(STATUS status) {
        ProjectContextHolder.setConsumeTime(Duration.ofMillis(System.currentTimeMillis() - startTime));
        ProjectContextHolder.setStatus(status);
        ProjectContextHolder.print();
    }
}
