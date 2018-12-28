package com.agile.common.cache.ehcache;

import com.agile.common.config.EhCacheConfig;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.EhCacheProperties;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.ehcache.internal.SingletonEhcacheRegionFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 佟盟 on 2018/1/4
 */
public class EhCacheRegionFactory extends SingletonEhcacheRegionFactory {
    private static final AtomicInteger REFERENCE_COUNT = new AtomicInteger();

    @Override
    protected CacheManager resolveCacheManager(SessionFactoryOptions settings, Map properties) {
        try {
            if (LoggerFactory.getCacheLog().isInfoEnabled()) {
                LoggerFactory.getCacheLog().info("初始化EhCache二级缓存区域");
            }
            REFERENCE_COUNT.incrementAndGet();
            return CacheManager.create(EhCacheConfig.configuration());
        } catch (Exception e) {
            if (LoggerFactory.getCacheLog().isInfoEnabled()) {
                LoggerFactory.getCacheLog().error("初始化EhCache二级缓存区域失败");
            }
            REFERENCE_COUNT.decrementAndGet();
            throw e;
        }
    }

    @Override
    protected Cache createCache(String regionName) {
        CacheManager cacheManager = CacheManager.getInstance();
        Cache cache = new Cache(new CacheConfiguration(regionName, (int) EhCacheProperties.getMaxEntriesLocalHeap())
                .timeToIdleSeconds(EhCacheProperties.getTimeToIdle())
                .timeToLiveSeconds(EhCacheProperties.getTimeToLive())
                .maxEntriesLocalDisk((int) EhCacheProperties.getMaxEntriesLocalDisk())
                .diskExpiryThreadIntervalSeconds(EhCacheProperties.getDiskExpiryThreadIntervalSeconds())
                .eternal(EhCacheProperties.isEternal())
                .diskSpoolBufferSizeMB(EhCacheProperties.getDiskSpoolBufferSize())
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.fromString(EhCacheProperties.getMemoryStoreEvictionPolicy())));
        cacheManager.addCache(cache);
        return cache;
    }
}
