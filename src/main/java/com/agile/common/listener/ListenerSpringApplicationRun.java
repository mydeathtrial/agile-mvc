package com.agile.common.listener;

import cloud.agileframework.spring.util.spring.PropertiesUtil;
import com.agile.common.container.AgileBanner;
import com.agile.common.listener.event.ListenerTest;
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
    private final SpringApplication application;
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
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
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

    public static long getConsume() {
        return System.currentTimeMillis() - startTime;
    }
}
