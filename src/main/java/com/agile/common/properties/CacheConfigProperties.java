package com.agile.common.properties;

import com.agile.common.annotation.Properties;

/**
 * Created by 佟盟 on 2018/2/1
 */
@Properties(prefix = "agile.cache")
public class CacheConfigProperties {
    private static String proxy = "ehcache";

    public static String getProxy() {
        return proxy;
    }

    public static void setProxy(String proxy) {
        CacheConfigProperties.proxy = proxy;
    }
}
