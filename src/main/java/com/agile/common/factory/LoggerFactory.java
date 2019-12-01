package com.agile.common.factory;

import com.agile.common.base.Constant;
import com.agile.common.container.WebInitializer;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.properties.LoggerProperties;
import com.agile.common.security.TokenFilter;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.util.array.ArrayUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.DeleteAction;
import org.apache.logging.log4j.core.appender.rolling.action.Duration;
import org.apache.logging.log4j.core.appender.rolling.action.IfAccumulatedFileSize;
import org.apache.logging.log4j.core.appender.rolling.action.IfFileName;
import org.apache.logging.log4j.core.appender.rolling.action.IfLastModified;
import org.apache.logging.log4j.core.appender.rolling.action.PathCondition;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.LevelRangeFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private static final String PATTERN_CONSOLE = "%highlight{%-d{yyyy-MM-dd HH:mm:ss} [ %clr{%5p} ] %clr{${sys:PID}}{magenta} %clr{---}{faint} %clr{[%15.15t]}{faint} [ %clr{%-40.40c{1.}}{cyan} ] %m%n%xwEx}" +
            "{FATAL=Bright Red, ERROR=Bright Magenta, WARN=Bright Yellow, INFO=Bright Green, DEBUG=Bright Cyan, TRACE=Bright White}";

    private static final String PATTERN_FILE = "%-d{yyyy-MM-dd HH:mm:ss} [%5p] ${sys:PID}--- [%15.15t] [ %-40.40c{1.} ] %m%n%xwEx";

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
            getLogger(null, packageName, levels);
        }
    }

    public static final Log COMMON_LOG = createLogger("container", WebInitializer.class, defaultLevels);
    public static final Log AUTHORITY_LOG = createLogger("authority", TokenFilter.class, defaultLevels);
    public static final Log CACHE_LOG = createLogger("cache", CacheUtil.class, defaultLevels);
    public static final Log DAO_LOG = createLogger("sql", Dao.class, defaultLevels);

    private static Map<Class<?>, Log> serviceCacheLogs = new HashMap<>();

    public static Log getServiceLog(Class<?> clazz) {
        Log log = serviceCacheLogs.get(clazz);
        if (log == null) {
            log = LoggerFactory.createLogger(Constant.FileAbout.SERVICE_LOGGER_FILE, clazz);
            serviceCacheLogs.put(clazz, log);
        }
        return log;
    }

    public static Log createLogger(String fileName, Class<?> clazz, String packagePath, Level... levels) {
        if (levels == null) {
            levels = new Level[]{Level.INFO, Level.DEBUG, Level.ERROR};
        }
        getLogger(fileName, packagePath, levels);
        return LogFactory.getLog(clazz);
    }

    public static Log createLogger(String fileName, Class<?> clazz, Level... levels) {
        if (levels == null || levels.length == 0) {
            levels = new Level[]{Level.INFO, Level.DEBUG, Level.ERROR};
        }
        getLogger(fileName, clazz.getPackage().getName(), levels);
        return LogFactory.getLog(clazz);
    }

    /**
     * 日志格式
     *
     * @return 日志格式
     */
    private static PatternLayout getPatternLayout(String pattern) {
        return PatternLayout.newBuilder()
                .withConfiguration(config)
                .withPattern(pattern)
                .build();
    }

    /**
     * 级别过滤器
     *
     * @param level 级别
     * @return 过滤器
     */
    private static LevelRangeFilter getFilter(Level level) {
        return LevelRangeFilter.createFilter(level, level, Filter.Result.ACCEPT, Filter.Result.DENY);
    }

    /**
     * 创建控制台输出引擎
     *
     * @param loggerName 日志名字
     * @return 引擎
     */
    private static ConsoleAppender getConsoleAppender(String loggerName, Level level) {
        if (!ArrayUtil.contains(defaultLevels, level)) {
            return null;
        }
        String name = loggerName + level.name() + "_console";
        if (config.getAppender(name) != null) {
            return config.getAppender(name);
        }
        ConsoleAppender consoleAppender = ConsoleAppender.newBuilder()
                .setName(name)
                .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
                .setIgnoreExceptions(true)
                .withBufferedIo(true)
                .setFilter(getFilter(level))
                .setLayout(getPatternLayout(PATTERN_CONSOLE))
                .build();
        consoleAppender.start();
        return consoleAppender;
    }

    /**
     * 滚动文件
     *
     * @param loggerName 日志名字
     * @return 输出引擎
     */
    private static RollingFileAppender getRollingFileAppender(String loggerName, Level level) {
        if (!ArrayUtil.contains(defaultLevels, level)) {
            return null;
        }
        String name = loggerName + level.name() + "_file";
        if (config.getAppender(name) != null) {
            return config.getAppender(name);
        }

        TriggeringPolicy policy;
        if (loggerProperties.getTriggerType() == LoggerProperties.TriggerType.SIZE) {
            policy = SizeBasedTriggeringPolicy.createPolicy(loggerProperties.getTriggerValue());
        } else {
            policy = TimeBasedTriggeringPolicy.newBuilder().withModulate(true).withInterval(Integer.parseInt(loggerProperties.getTriggerValue())).build();
        }

        IfFileName.createNameCondition("*.log.gz",null);
        DeleteAction deleteAction = DeleteAction.createDeleteAction(path + "old",
                false,
                3,
                false,
                null,
                new PathCondition[]{IfLastModified.createAgeCondition(Duration.parse(loggerProperties.getTimeout().getSeconds() + "s")), IfAccumulatedFileSize.createFileSizeCondition(loggerProperties.getMaxSize())},
                null,
                config
        );

        RollingFileAppender appender = RollingFileAppender
                .newBuilder()
                .setName(name)
                .withFileName(loggerName.contains(".") ? String.format(path + "%s.log", level.name().toLowerCase()) : String.format(path + loggerName + "/%s.log", level.name().toLowerCase()))
                .withFilePattern(loggerName.contains(".") ? path + "old/%d{yyyy-MM-dd}-" + level.name().toLowerCase() + ".log.gz" : path + "old/%d{yyyy-MM-dd}-" + loggerName + ".log.gz")
                .withAppend(true)
                .withLocking(false)
                .setFilter(getFilter(level))
                .setIgnoreExceptions(true)
                .withBufferedIo(true)
                .setLayout(getPatternLayout(PATTERN_FILE))
                .withPolicy(policy)
                .withStrategy(DefaultRolloverStrategy.newBuilder().withCustomActions(new Action[]{deleteAction}).build()).build();
        appender.start();

        return appender;
    }

    /**
     * 初始化日志配置
     *
     * @param loggerName  日志名
     * @param packageName 包名
     * @param levels      级别
     */
    private static void getLogger(String loggerName, String packageName, Level... levels) {
        if (loggerName == null) {
            loggerName = packageName;
        }
        String finalLoggerName = loggerName;

        Set<Appender> appenderSet = Stream.of(levels)
                .map(level ->
                        getConsoleAppender(finalLoggerName, level)
                ).filter(Objects::nonNull).collect(Collectors.toSet());

        appenderSet.addAll(Stream.of(levels)
                .map(level ->
                        getRollingFileAppender(finalLoggerName, level)
                ).filter(Objects::nonNull).collect(Collectors.toSet()));

        if (!loggerName.equals(packageName)) {
            appenderSet.addAll(Stream.of(levels)
                    .map(level ->
                            getRollingFileAppender(packageName, level)
                    ).filter(Objects::nonNull).collect(Collectors.toSet()));
        }

        AppenderRef[] refs = appenderSet.stream()
                .peek(appender -> config.addAppender(appender))
                .map(rollingFileAppender -> AppenderRef.createAppenderRef(rollingFileAppender.getName(), null, null))
                .collect(Collectors.toSet()).toArray(new AppenderRef[]{});


        LoggerConfig loggerConfig = AsyncLoggerConfig.createLogger(Boolean.FALSE,
                Level.ALL,
                packageName,
                Boolean.TRUE.toString(),
                refs,
                null,
                config,
                null);

        appenderSet.forEach(appender -> loggerConfig.addAppender(appender, null, null));

        config.addLogger(packageName, loggerConfig);
        ctx.updateLoggers();
    }

    public static void stop(String loggerName) {
        Level[] levels = Level.values();
        for (Level level : levels) {
            String filename = StringUtil.camelToUnderline(loggerName) + "_" + level.name();
            config.getRootLogger().removeAppender(filename);
        }
        ctx.updateLoggers();
    }

}
