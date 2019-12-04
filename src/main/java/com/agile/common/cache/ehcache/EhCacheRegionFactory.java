package com.agile.common.cache.ehcache;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.util.FactoryUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.ehcache.internal.SingletonEhcacheRegionFactory;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 佟盟 on 2018/1/4
 */
public class EhCacheRegionFactory extends SingletonEhcacheRegionFactory {
    private static final AtomicInteger REFERENCE_COUNT = new AtomicInteger();

    @Override
    protected CacheManager resolveCacheManager(SessionFactoryOptions settings, Map properties) {
        try {
            if (LoggerFactory.CACHE_LOG.isInfoEnabled()) {
                LoggerFactory.CACHE_LOG.info("完成初始化EhCache二级缓存区域");
            }
            REFERENCE_COUNT.incrementAndGet();
            return Objects.requireNonNull(FactoryUtil.getBean(EhCacheCacheManager.class)).getCacheManager();
        } catch (Exception e) {
            if (LoggerFactory.CACHE_LOG.isErrorEnabled()) {
                LoggerFactory.CACHE_LOG.error("初始化EhCache二级缓存区域失败", e);
            }
            REFERENCE_COUNT.decrementAndGet();
            throw e;
        }
    }

    @Override
    protected Cache createCache(String regionName) {
        CacheManager cacheManager = Objects.requireNonNull(FactoryUtil.getBean(EhCacheCacheManager.class)).getCacheManager();
        assert cacheManager != null;
        return (Cache) cacheManager.addCacheIfAbsent(regionName);
    }
}
