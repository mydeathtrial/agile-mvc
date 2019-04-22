package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "spring.ehcache")
@Setter
@Getter
public class EhCacheProperties {
    /**
     * 默认配置
     */
    private String defaultConfigName;
    /**
     * 存储地址
     */
    private String path;

    /**
     * 缓存区域配置
     */
    private Map<String, CacheConfiguration> regions = new HashMap<>();
}
