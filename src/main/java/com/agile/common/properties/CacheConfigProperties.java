package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.cache")
@Setter
@Getter
public class CacheConfigProperties {
    /**
     * 第三方代理缓存组件
     */
    private CacheProxy proxy = CacheProxy.EHCACHE;

    /**
     * 缓存组件实现方
     */
    public enum CacheProxy {
        /**
         * ehcache内存缓存组件，建议单应用部署时使用
         */
        EHCACHE,
        /**
         * redis缓存组件，建议集群部署时使用
         */
        REDIS
    }
}
