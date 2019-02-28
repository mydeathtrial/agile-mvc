package com.agile.common.util;

import com.agile.common.cache.Cache;
import com.agile.common.cache.CustomCacheManager;
import com.agile.common.factory.LoggerFactory;

import java.util.Objects;

/**
 * @author 佟盟 on 2017/5/19
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
        LoggerFactory.COMMON_LOG.info(String.format("缓存存放:[key:%s][value:%s]", o1, o2));
        getCache().put(o1, o2);
    }

    public static Object get(Object o) {

        if (!containKey(o)) {
            return null;
        }
        Object o2 = Objects.requireNonNull(getCache().get(o)).get();
        LoggerFactory.COMMON_LOG.info(String.format("缓存获取:[key:%s][value:%s]", o, o2));
        return o2;
    }

    public static <T> T get(Object o1, Class<T> o2) {
        T o3 = getCache().get(o1, o2);
        LoggerFactory.COMMON_LOG.info(String.format("缓存获取:[key:%s][value:%s][type:%s]", o1, o3, o2));
        return o3;
    }

    public static void evict(Object o) {
        LoggerFactory.COMMON_LOG.info(String.format("缓存删除:[key:%s]", o));
        getCache().evict(o);
    }

    public static boolean containKey(Object o) {
        return !ObjectUtil.isEmpty(getCache().get(o));
    }

    public static Long setNx(String key, String value) {
        LoggerFactory.COMMON_LOG.info(String.format("缓存同步锁:[key:%s][value:%s]", key, value));
        return getCache().setNx(key, value);
    }

    public static void expire(String lockName, int timeout) {
        LoggerFactory.COMMON_LOG.info(String.format("缓存过期设置:[key:%s][timeout:%s]", lockName, timeout));
        getCache().expire(lockName, timeout);
    }

    public static void put(Object o1, Object o2, int timeout) {
        LoggerFactory.COMMON_LOG.info(String.format("缓存过期设置:[key:%s][value:%s][timeout:%s]", o1, o2, timeout));
        getCache().put(o1, o2, timeout);
    }
}
