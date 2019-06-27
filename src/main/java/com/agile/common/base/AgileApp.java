package com.agile.common.base;

import com.agile.common.config.LoggerFactoryConfig;
import com.agile.common.container.AgileBanner;
import com.agile.common.util.PropertiesUtil;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.springframework.boot.SpringApplication;

/**
 * 描述：
 * <p>创建时间：2019/1/9<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class AgileApp {
    private static long startTime;

    public static void run(Class<?> primarySource, String[] args) {
        startTime = System.currentTimeMillis();
        ConfigurationFactory.setConfigurationFactory(new LoggerFactoryConfig());
        SpringApplication app = new SpringApplication(primarySource);
        app.setBanner(new AgileBanner());
        app.setDefaultProperties(PropertiesUtil.getProperties());
        app.run(args);
    }

    public static long getConsumeTime() {
        return (System.currentTimeMillis() - startTime) / Constant.NumberAbout.THOUSAND;
    }

}
