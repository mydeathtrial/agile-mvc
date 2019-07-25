package com.agile.common.cache.ehcache;

import com.agile.common.cache.AgileCache;
import com.agile.common.cache.AgileCacheManager;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

/**
 * @author 佟盟
 * 日期 2019/7/22 17:14
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Component
@ConditionalOnProperty(name = "type", prefix = "spring.cache", havingValue = "ehcache")
public class AgileEhCacheCacheManager extends AgileCacheManager {

    private EhCacheCacheManager cacheManager;

    @Autowired
    public AgileEhCacheCacheManager(EhCacheCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public AgileCache cover(Cache cache) {
        return new AgileEhCache((EhCacheCache) cache);
    }

    @Override
    public AgileCache getMissingCache(String cacheName) {
        CacheManager ehcacheCacheManager = cacheManager.getCacheManager();
        assert ehcacheCacheManager != null;
        Ehcache target = ehcacheCacheManager.getEhcache(cacheName);
        if (target == null) {
            target = ehcacheCacheManager.addCacheIfAbsent(cacheName);
        }
        return cover(new EhCacheCache(target));
    }
}
