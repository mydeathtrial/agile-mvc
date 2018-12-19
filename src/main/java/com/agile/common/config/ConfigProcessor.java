package com.agile.common.config;

import com.agile.common.base.Constant;
import com.agile.common.util.PropertiesUtil;

/**
 * 描述：
 * <p>创建时间：2018/11/29<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class ConfigProcessor {
    private enum ConfigInfo {
        Swagger2Config("agile.swagger.enable", Swagger2Config.class),
        KaptchaConfig("agile.kaptcha.enable", KaptchaConfig.class),
        TaskConfig("agile.task.enable", TaskConfig.class),
        ESConfig("agile.elasticsearch.enable", ESConfig.class),
        ActivitiConfig("agile.activiti.enable", ActivitiConfig.class),
        SecurityConfig("agile.security.enable", SecurityConfig.class);

        private String propKey;
        private Class[] configClass;

        ConfigInfo(String propKey, Class... configClass) {
            this.propKey = propKey;
            this.configClass = configClass;
        }

        public String getPropKey() {
            return propKey;
        }

        public Class[] getConfigClass() {
            return configClass;
        }
    }

    public static String contextConfigLocation() {
        StringBuilder builder = new StringBuilder("com.agile.common.config.SpringConfig;com.agile.common.config.SpringMvcConfig;");
        for (ConfigInfo configInfo : ConfigInfo.values()) {
            if (PropertiesUtil.getProperty(configInfo.getPropKey(), boolean.class, "false")) {
                Class[] classes = configInfo.getConfigClass();
                for (Class clazz : classes) {
                    builder.append(clazz.getName() + Constant.RegularAbout.SEMICOLON);
                }
            }
        }

        String cacheProxy = PropertiesUtil.getProperty("agile.cache.proxy").toLowerCase();
        switch (cacheProxy) {
            case "redis":
                builder.append(RedisConfig.class.getName() + Constant.RegularAbout.SEMICOLON);
                break;
            case "ehcache":
                builder.append(EhCacheConfig.class.getName() + Constant.RegularAbout.SEMICOLON);
                break;
        }
        return builder.toString();
    }
}
