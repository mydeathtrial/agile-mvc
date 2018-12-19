package com.agile.common.cache.ehCache;

import com.agile.common.cache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * Created by 佟盟 on 2018/9/6
 */
public class EhCacheCache extends org.springframework.cache.ehcache.EhCacheCache implements Cache {
    private Ehcache cache;

    public EhCacheCache(Ehcache ehcache) {
        super(ehcache);
        this.cache = ehcache;
    }

    @Override
    public void put(Object key, Object value, int timeout) {
        Element e = new Element(key, value);
        e.setTimeToLive(timeout);
        cache.put(e);
    }

    @Override
    public <T> T getCollection(Object o1, Class<T> o2) {
        try {
            return (T) get(o1).get();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Long setNx(String key, String value) {
        return null;
    }

    @Override
    public void expire(String lockName, int time) {

    }
}
