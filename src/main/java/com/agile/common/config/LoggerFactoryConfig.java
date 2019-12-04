package com.agile.common.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.net.URI;

/**
 * @author 佟盟 on 2017/11/2
 */
@Order(1)
@Plugin(name = "LoggerFactoryConfig", category = ConfigurationFactory.CATEGORY)
public class LoggerFactoryConfig extends ConfigurationFactory {
    private static final String PATTERN = "%highlight{%-d{yyyy-MM-dd HH:mm:ss} [ %clr{%5p} ] %clr{${sys:PID}}{magenta} %clr{---}{faint} %clr{[%15.15t]}{faint} [ %clr{%-40.40c{1.}}{cyan} ] %m%n%xwEx}" +
            "{FATAL=Bright Red, ERROR=Bright Magenta, WARN=Bright Yellow, INFO=Bright Green, DEBUG=Bright Cyan, TRACE=Bright White}";

    private static Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
        builder.setConfigurationName(name);
        final LayoutComponentBuilder layout = builder.newLayout("PatternLayout").addAttribute("pattern", PATTERN);

        //log4j2自身内部日志级别
        builder.setStatusLevel(Level.OFF);

        //控制台日志
        AppenderComponentBuilder consoleConfig = builder.newAppender("Stdout", "CONSOLE").addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT).addComponent(layout);
        builder.add(consoleConfig);

        builder.add(builder.newAsyncRootLogger(Level.OFF).add(builder.newAppenderRef("Stdout")));
        return builder.build();

    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[]{"*"};
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource configurationSource) {
        return getConfiguration(loggerContext, configurationSource.toString(), null);
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, String name, URI configLocation) {
        ConfigurationFactory.setConfigurationFactory(new LoggerFactoryConfig());
        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        return createConfiguration(name, builder);
    }
}
