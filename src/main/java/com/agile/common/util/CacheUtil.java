package com.agile.common.util;

import com.agile.common.cache.Cache;
import com.agile.common.cache.CustomCacheManager;

import java.util.Objects;

/**
 * Created by 佟盟 on 2017/5/19
 */
public class CacheUtil {

    private static Cache cache;

    public static Cache getCache(String cacheName) {
        return FactoryUtil.getBean(CustomCacheManager.class).getCustomCache(cacheName);
    }

    public static Cache getCache() {
        if (ObjectUtil.isEmpty(cache)) {
            cache = getCache("agileCache");
        }
        return cache;
    }

    public static void put(Object o1, Object o2) {

        getCache().put(o1, o2);
    }

    public static Object get(Object o) {
        if (!containKey(o)) {
            return null;
        }
        return Objects.requireNonNull(getCache().get(o)).get();
    }

    public static <T> T get(Object o1, Class<T> o2) {
        return getCache().get(o1, o2);
    }

    public static void evict(Object o) {
        getCache().evict(o);
    }

    public static boolean containKey(Object o) {
        return !ObjectUtil.isEmpty(getCache().get(o));
    }

    public static Long setNx(String key, String value) {
        return getCache().setNx(key, value);
    }

    public static void expire(String lockName, int timeout) {
        getCache().expire(lockName, timeout);
    }

    public static void put(Object o1, Object o2, int timeout) {
        getCache().put(o1, o2, timeout);
    }
}
