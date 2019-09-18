package com.agile.common.factory;

import com.agile.common.base.Constant;
import com.agile.common.container.WebInitializer;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.properties.LoggerProperties;
import com.agile.common.security.TokenFilter;
import com.agile.common.util.ArrayUtil;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.FactoryUtil;
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
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.LevelRangeFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.agile.common.base.Constant.NumberAbout.TWO;

/**
 * @author 佟盟 on n017/TWO/n3
 * 日志工厂
 */
public final class LoggerFactory {
    private static LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    private static Configuration config = ctx.getConfiguration();
    private static LoggerProperties loggerProperties = FactoryUtil.getBean(LoggerProperties.class);
    private static Level[] defaultLevels = loggerProperties != null ? loggerProperties.getLevels() : new Level[]{Level.DEBUG, Level.INFO, Level.ERROR};
    private static String path;
    private static final String PATTERN = "%highlight{%-d{yyyy-MM-dd HH:mm:ss} [ %clr{%5p} ] %clr{${sys:PID}}{magenta} %clr{---}{faint} %clr{[%15.15t]}{faint} [ %clr{%-40.40c{1.}}{cyan} ] %m%n%xwEx}" +
            "{FATAL=Bright Red, ERROR=Bright Magenta, WARN=Bright Yellow, INFO=Bright Green, DEBUG=Bright Cyan, TRACE=Bright White}";

    static {

        if (loggerProperties == null) {
            throw new RuntimeException();
        }
        path = loggerProperties.getPackageUri();
        if (!path.endsWith(Constant.RegularAbout.BACKSLASH) && !path.endsWith(Constant.RegularAbout.SLASH)) {
            path += Constant.RegularAbout.SLASH;
        }
        loggerProperties.setPackageUri(path);
        if (!path.endsWith(Constant.RegularAbout.SLASH)) {
            path += Constant.RegularAbout.SLASH;
        }

        for (Map.Entry<String, Level[]> packageInfo : loggerProperties.getPackageName().entrySet()) {
            Level[] levels = packageInfo.getValue();
            String packageName = packageInfo.getKey();
            createLogger(packageName, packageName, levels);
        }
    }

    public static final Log COMMON_LOG = createLogger("container", WebInitializer.class, defaultLevels);
    public static final Log AUTHORITY_LOG = createLogger("authority", TokenFilter.class, defaultLevels);
    public static final Log CACHE_LOG = createLogger("cache", CacheUtil.class, defaultLevels);
    public static final Log DAO_LOG = createLogger("sql", Dao.class, defaultLevels);

    private static Map<Class, Log> serviceCacheLogs = new HashMap<>();

    public static void stop(String loggerName) {
        Level[] levels = Level.values();
        for (Level level : levels) {
            String filename = StringUtil.camelToUnderline(loggerName) + "_" + level.name();
            config.getRootLogger().removeAppender(filename);
        }
        ctx.updateLoggers();
    }

    private static Log createPlugLogger(String fileName, String plugKey, Class clazz, Level... levels) {
        if (!PropertiesUtil.getProperty(plugKey, boolean.class, Boolean.FALSE.toString())) {
            return null;
        }
        return createLogger(fileName, clazz, levels);
    }

    public static Log getServiceLog(Class clazz) {
        Log log = serviceCacheLogs.get(clazz);
        if (log == null) {
            log = LoggerFactory.createLogger(Constant.FileAbout.SERVICE_LOGGER_FILE, clazz);
            serviceCacheLogs.put(clazz, log);
        }
        return log;
    }

    private static void createLogger(String loggerName, String packagePath, Level... levels) {
        PatternLayout layout = PatternLayout.newBuilder()
                .withConfiguration(config)
                .withPattern(PATTERN)
                .build();

        levels = Arrays.stream(levels).filter(level -> ArrayUtil.contains(defaultLevels, level)).collect(Collectors.toList()).toArray(new Level[]{});
        AppenderRef[] refs = new AppenderRef[levels.length * TWO];
        String[] appenders = new String[levels.length * TWO];
        for (int i = 0; i < levels.length; i++) {
            String targetFileName = (loggerName + "_" + levels[i].name()).toLowerCase();
            if (ObjectUtil.isEmpty(config.getAppender(targetFileName))) {
                //输出引擎
                appenders[i * TWO] = createFileAppender(loggerName, targetFileName, layout);
                refs[i * TWO] = AppenderRef.createAppenderRef(appenders[i * TWO], null, null);

                appenders[i * TWO + 1] = createConsoleAppender(targetFileName, layout);
                refs[i * TWO + 1] = AppenderRef.createAppenderRef(appenders[i * TWO + 1], null, null);
            }
        }
        LoggerConfig loggerConfig = AsyncLoggerConfig.createLogger(Boolean.FALSE, Level.ALL, packagePath, Boolean.TRUE.toString(), refs, null, config, null);
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

    private static String createFileAppender(String loggerName, String appenderName, PatternLayout layout) {
        appenderName += "_file";

        if (ObjectUtil.isEmpty(config.getAppender(appenderName))) {

            TriggeringPolicy policy;
            if (loggerProperties.getTriggerType() == LoggerProperties.TriggerType.SIZE) {
                policy = SizeBasedTriggeringPolicy.createPolicy(loggerProperties.getTriggerValue());
            } else {
                policy = TimeBasedTriggeringPolicy.newBuilder().withModulate(true).withInterval(Integer.parseInt(loggerProperties.getTriggerValue())).build();
            }

            Appender appender = RollingFileAppender
                    .newBuilder()
                    .setName(appenderName)
                    .withFileName(String.format(path + loggerName + "/%s.log", appenderName))
                    .withFilePattern(path + "logs/%d{yyyy-MM-dd}/" + loggerName + ".log")
                    .withAppend(true)
                    .withLocking(false)
                    .setIgnoreExceptions(true)
                    .withBufferedIo(true)
                    .setLayout(layout)
                    .withPolicy(policy)
                    .withStrategy(DefaultRolloverStrategy.newBuilder().withMax("100").build()).build();
            appender.start();

            config.addAppender(appender);
        }
        return appenderName;
    }

    private static String createConsoleAppender(String appenderName, Layout<String> layout) {
        appenderName += "_console";
        if (ObjectUtil.isEmpty(config.getAppender(appenderName))) {
            Appender consoleAppender = ConsoleAppender.newBuilder().withName(appenderName).setTarget(ConsoleAppender.Target.SYSTEM_OUT).withIgnoreExceptions(true).withBufferedIo(true).withLayout(layout).build();
            consoleAppender.start();
            config.addAppender(consoleAppender);
        }
        return appenderName;
    }

    public static Log createLogger(String fileName, Class clazz, String packagePath, Level... levels) {
        if (levels == null) {
            levels = new Level[]{Level.INFO, Level.DEBUG, Level.ERROR};
        }
        createLogger(fileName, packagePath, levels);
        return LogFactory.getLog(clazz);
    }

    public static Log createLogger(String fileName, Class clazz, Level... levels) {
        if (levels == null || levels.length == 0) {
            levels = new Level[]{Level.INFO, Level.DEBUG, Level.ERROR};
        }
        createLogger(fileName, clazz.getName(), levels);
        return LogFactory.getLog(clazz);
    }
}
