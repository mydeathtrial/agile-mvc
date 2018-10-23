package com.agile.common.cache.ehCache;

import com.agile.common.cache.CustomCacheManager;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Created by 佟盟 on 2018/9/6
 */
public class EhCacheCacheManager extends org.springframework.cache.ehcache.EhCacheCacheManager implements CustomCacheManager {
    public EhCacheCacheManager(CacheManager object) {
        super(object);
    }

    @Override
    protected Collection<Cache> loadCaches() {
        CacheManager cacheManager = this.getCacheManager();
        Assert.state(cacheManager != null, "No CacheManager set");
        Status status = cacheManager.getStatus();
        if (!Status.STATUS_ALIVE.equals(status)) {
            throw new IllegalStateException("An 'alive' EhCache CacheManager is required - current cache is " + status.toString());
        } else {
            String[] names = this.getCacheManager().getCacheNames();
            LinkedHashSet<Cache> caches = new LinkedHashSet<>(names.length);

            for(int i = 0; i < names.length; ++i) {
                String name = names[i];
                caches.add(new EhCacheCache(this.getCacheManager().getEhcache(name)));
            }

            return caches;
        }
    }

    @Override
    public com.agile.common.cache.Cache getCustomCache(String cacheName) {
        return (com.agile.common.cache.Cache) getCache(cacheName);
    }
}
