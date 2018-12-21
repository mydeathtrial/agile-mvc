package com.agile.common.config;

import com.agile.common.base.Constant;
import com.agile.common.util.PropertiesUtil;

/**
 * 描述：组件配置处理器（暂未使用）
 * <p>创建时间：2018/11/29<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class ConfigProcessor {
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
            default:
        }
        return builder.toString();
    }

    /**
     * 组件配置信息
     */
    private enum ConfigInfo {
        /**
         * Swagger2配置
         */
        Swagger2Config("agile.swagger.enable", Swagger2Config.class),

        /**
         * Kaptcha配置
         */
        KaptchaConfig("agile.kaptcha.enable", KaptchaConfig.class),

        /**
         * Task配置
         */
        TaskConfig("agile.task.enable", TaskConfig.class),

        /**
         * ES配置
         */
        ESConfig("agile.elasticsearch.enable", ESConfig.class),

        /**
         * Activiti配置
         */
        ActivitiConfig("agile.activiti.enable", ActivitiConfig.class),

        /**
         * Security配置
         */
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
}
