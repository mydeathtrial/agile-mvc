package com.agile.common.cache;

/**
 * @author 佟盟 on 2018/9/6
 */
public interface CustomCacheManager {

    Cache getCustomCache(String cacheName);
}
