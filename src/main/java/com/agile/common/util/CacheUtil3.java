package com.agile.common.util;

/**
 * @author 佟盟 on 2017/5/19
 */
public class CacheUtil3 {
//    private static RedisTemplate redisTemplate;
//
//    private static RedisTemplate getRedisTemplate() {
//        if (ObjectUtil.isEmpty(redisTemplate)) {
//            redisTemplate = (RedisTemplate) FactoryUtil.getBean("redisTemplate");
//        }
//        return redisTemplate;
//    }
//
//    public static Cache getCache(String cacheName) {
//        CacheManager cacheManager = FactoryUtil.getBean(CacheManager.class);
//        if (cacheManager == null) {
//            throw new RuntimeException("not found CacheManager");
//        }
//        return cacheManager.getCache(cacheName);
//    }
//
//    public static Cache getDicCache() {
//        return getCache("dictionary-cache");
//    }
//
//    public static Cache getCache() {
//        return getCache("common-cache");
//    }
//
//    public static void put(Object key, Object value) {
//        LoggerFactory.COMMON_LOG.info(String.format("缓存存放:[key:%s][value:%s]", key, value));
//        getCache().put(key, value);
//    }
//
//    public static Object get(Object key) {
//        Object result = null;
//        Cache.ValueWrapper value = getCache().get(key);
//        if (value != null) {
//            result = value.get();
//        }
//        LoggerFactory.COMMON_LOG.info(String.format("缓存获取:[key:%s][value:%s]", key, result));
//        return result;
//    }
//
//    public static <T> T get(Object key, Class<T> clazz) {
//        T value = getCache().get(key, clazz);
//        LoggerFactory.COMMON_LOG.info(String.format("缓存获取:[key:%s][value:%s][type:%s]", key, value, clazz));
//        return value;
//    }
//
//    public static void evict(Object key) {
//        LoggerFactory.COMMON_LOG.info(String.format("缓存删除:[key:%s]", key));
//        getCache().evict(key);
//    }
//
//    public static boolean containKey(Object o) {
//        return !ObjectUtil.isEmpty(getCache().get(o));
//    }
//
//    public static Object putIfAbsent(String key, String value) {
//        Cache.ValueWrapper valueWrapper = getCache().putIfAbsent(key, value);
//        if (valueWrapper != null) {
//            Object v = valueWrapper.get();
//            if (v != null && v.equals(value)) {
//                LoggerFactory.COMMON_LOG.info(String.format("缓存存放:[key:%s][value:%s]", key, value));
//                return v;
//            }
//        }
//        return null;
//    }
//
//    public static void put(Object o1, Object o2, int timeout) {
//        LoggerFactory.COMMON_LOG.info(String.format("缓存过期设置:[key:%s][value:%s][timeout:%s]", o1, o2, timeout));
//        getCache().put(o1, o2, timeout);
//    }
}
