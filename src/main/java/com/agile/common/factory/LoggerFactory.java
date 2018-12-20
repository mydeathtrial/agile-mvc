package com.agile.common.factory;

import com.agile.common.base.Constant;
import com.agile.common.cache.Cache;
import com.agile.common.config.LoggerFactoryConfig;
import com.agile.common.container.WebInitializer;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.PropertiesUtil;
import com.agile.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.LevelRangeFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.elasticsearch.client.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by 佟盟 on n017/TWO/n3
 * 日志工厂
 */
public final class LoggerFactory {
    private static LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    private static Configuration config = ctx.getConfiguration();
    private static String path = PropertiesUtil.getProperty("agile.log.package_uri", System.getProperty("webapp.root", "") + "logs\\");
    private static Map<Class, Log> serviceCacheLogs = new HashMap<>();

    private static Log esLog = createPlugLogger("elasticsearch", "agile.elasticsearch.enable", Client.class, Level.INFO, Level.ERROR);
    private static Log taskLog = createPlugLogger("task", "agile.task.enable", Client.class, Level.INFO, Level.ERROR);
    private static Log commonLog = createLogger("container", WebInitializer.class, Level.DEBUG, Level.ERROR);
    private static Log cacheLog = createLogger("cache", Cache.class, Level.DEBUG, Level.ERROR);
    private static Log daoLog = createLogger("sql", Dao.class, Level.INFO, Level.ERROR);
    private static final int TWO = 2;
    
    public static Log getEsLog() {
        return esLog;
    }

    public static Log getTaskLog() {
        return taskLog;
    }

    public static Log getCommonLog() {
        return commonLog;
    }

    public static Log getCacheLog() {
        return cacheLog;
    }

    public static Log getDaoLog() {
        return daoLog;
    }

    static {
        ConfigurationFactory.setConfigurationFactory(new LoggerFactoryConfig());
        String prefix = "agile.log.package.";
        Properties properties = PropertiesUtil.getPropertyByPrefix(prefix);
        for (String key : properties.stringPropertyNames()) {
            String[] levels = PropertiesUtil.getProperty(key).split(Constant.RegularAbout.COMMA);
            String packageName = key.replaceFirst(prefix, "");
            createLogger(packageName, packageName, coverLevel(levels));
        }
    }

    private LoggerFactory() {
    }

    private static void createLogger(String baseName, String packagePath, Level... levels) {
        //创建输出格式
        Layout layout = PatternLayout
                .newBuilder()
                .withConfiguration(config)
                .withPattern("%highlight{%-d{yyyy-MM-dd HH:mm:ss} [ %p ] [ %c ] %m%n}{FATAL=Bright Red, ERROR=Bright Magenta, WARN=Bright Yellow, INFO=Bright Green, DEBUG=Bright Cyan, TRACE=Bright White}")
                .build();
        baseName = StringUtil.camelToUnderline(baseName);
        AppenderRef[] refs = new AppenderRef[levels.length * TWO];
        String[] appenders = new String[levels.length * TWO];
        for (int i = 0; i < levels.length; i++) {
            String targetFileName = (baseName + "_" + levels[i].name()).toLowerCase();
            if (ObjectUtil.isEmpty(config.getAppender(targetFileName))) {
                //输出引擎
                appenders[i * TWO] = createFileAppender(baseName, targetFileName, layout);
                refs[i * TWO] = AppenderRef.createAppenderRef(appenders[i * TWO], null, null);

                appenders[i * TWO + 1] = createConsoleAppender(targetFileName, layout);
                refs[i * TWO + 1] = AppenderRef.createAppenderRef(appenders[i * TWO + 1], null, null);
            }
        }
        LoggerConfig loggerConfig = AsyncLoggerConfig.createLogger(Boolean.FALSE, Level.ALL, packagePath, "true", refs, null, config, null);
        for (int i = 0; i < levels.length; i++) {
            if (ObjectUtil.isEmpty(appenders[i])) {
                break;
            }
            Filter filter = LevelRangeFilter.createFilter(levels[i], levels[i], Filter.Result.ACCEPT, Filter.Result.DENY);
            loggerConfig.addAppender(config.getAppender(appenders[i * TWO]), levels[i], filter);
            loggerConfig.addAppender(config.getAppender(appenders[i * TWO + 1]), levels[i], filter);
        }
        config.addLogger(packagePath, loggerConfig);
        ctx.updateLoggers();
    }

    private static String createFileAppender(String baseName, String fileName, Layout layout) {
        String name = fileName + "_file";
        if (ObjectUtil.isEmpty(config.getAppender(name))) {

            TriggeringPolicy policy;
            String type = PropertiesUtil.getProperty("agile.log.trigger_type", "time");
            String valueKey = "agile.log.trigger_value";
            if ("time".equals(type)) {
                policy = TimeBasedTriggeringPolicy.newBuilder().withModulate(true).withInterval(PropertiesUtil.getProperty(valueKey, int.class, "1")).build();
            } else {
                policy = SizeBasedTriggeringPolicy.createPolicy(PropertiesUtil.getProperty(valueKey, "1M"));
            }

            Appender appender = RollingFileAppender.newBuilder().withName(name).withFileName(String.format(path + baseName + "/%s.log", fileName)).withFilePattern(path + "logs/%d{yyyy-MM-dd}/" + baseName + "/" + fileName + ".log")
                    .withAppend(true)
                    .withLocking(false)
                    .withIgnoreExceptions(true)
                    .withBufferedIo(true)
                    .withLayout(layout)
                    .withPolicy(policy)
                    .withStrategy(DefaultRolloverStrategy.newBuilder().withMax("100").build()).build();
            appender.start();
            config.addAppender(appender);
        }
        return name;
    }

    private static String createConsoleAppender(String name, Layout layout) {
        name += "_console";
        if (ObjectUtil.isEmpty(config.getAppender(name))) {
            @SuppressWarnings("unchecked")
            Appender consoleAppender = ConsoleAppender.newBuilder().withName(name).setTarget(ConsoleAppender.Target.SYSTEM_OUT).withIgnoreExceptions(true).withBufferedIo(true).withLayout(layout).build();
            consoleAppender.start();
            config.addAppender(consoleAppender);
        }
        return name;
    }

    public static void stop(String fileName) {
        Level[] levels = Level.values();
        for (int i = 0; i < levels.length; i++) {
            String filename = StringUtil.camelToUnderline(fileName) + "_" + levels[i].name();
            config.getRootLogger().removeAppender(filename);
        }
        ctx.updateLoggers();
    }

    public static Log createLogger(String fileName, Class clazz) {
        createLogger(fileName, clazz.getName(), Level.INFO, Level.ERROR);
        return LogFactory.getLog(clazz);
    }

    public static Log createLogger(String fileName, Class clazz, Level... levels) {
        createLogger(fileName, clazz.getName(), levels);
        return LogFactory.getLog(clazz);
    }

    private static Log createPlugLogger(String fileName, String plugKey, Class clazz, Level... levels) {
        if (!PropertiesUtil.getProperty(plugKey, boolean.class, "false")) {
            return null;
        }
        return createLogger(fileName, clazz, levels);
    }

    public static Log createLogger(String fileName, Class clazz, String packagePath, Level... levels) {
        createLogger(fileName, packagePath, levels);
        return LogFactory.getLog(clazz);
    }

    public static Log createLogger(String fileName, Class clazz, String packagePath) {
        createLogger(fileName, packagePath, Level.INFO, Level.ERROR);
        return LogFactory.getLog(clazz);
    }

    private static Level[] coverLevel(String[] levels) {

        ArrayList<Level> list = new ArrayList<Level>();
        for (String level : levels) {
            switch (level.toUpperCase()) {
                case "OFF":
                    list.add(Level.OFF);
                    break;
                case "FATAL":
                    list.add(Level.FATAL);
                    break;
                case "ERROR":
                    list.add(Level.ERROR);
                    break;
                case "WARN":
                    list.add(Level.WARN);
                    break;
                case "INFO":
                    list.add(Level.INFO);
                    break;
                case "DEBUG":
                    list.add(Level.DEBUG);
                    break;
                case "TRACE":
                    list.add(Level.TRACE);
                    break;
                case "ALL":
                    list.add(Level.ALL);
                    break;
                default:
                    list.add(Level.OFF);
            }
        }
        return list.toArray(new Level[]{});
    }

    public static Log getServiceLog(Class clazz) {
        Log log = serviceCacheLogs.get(clazz);
        if (log == null) {
            log = LoggerFactory.createLogger(Constant.FileAbout.SERVICE_LOGGER_FILE, clazz);
            serviceCacheLogs.put(clazz, log);
        }
        return log;
    }
}
