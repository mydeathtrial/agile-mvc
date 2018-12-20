package com.agile.common.cache.redis;

import com.agile.common.factory.LoggerFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.springframework.cache.Cache;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.cache.RedisCache;

/**
 * Created by 佟盟 on 2018/5/10
 */
public class StorageAccessImpl implements DomainDataStorageAccess {
    private final RedisCache cache;

    StorageAccessImpl(RedisCache cache) {
        this.cache = cache;
    }

    public RedisCache getCache() {
        return cache;
    }

    @Override
    public Object getFromCache(Object key, SharedSessionContractImplementor session) {
        try {
            final Cache.ValueWrapper element = getCache().get(key);
            if (element == null) {
                return null;
            } else {
                return element.get();
            }
        } catch (Exception e) {
            if (LoggerFactory.getCacheLog().isErrorEnabled()) {
                if (e instanceof RedisConnectionFailureException) {
                    LoggerFactory.getCacheLog().error("连接Redis失败");
                    System.exit(0);
                } else {
                    LoggerFactory.getCacheLog().error("redis缓存提取失败");
                }
            }
            throw new CacheException(e);
        }
    }

    @Override
    public void putIntoCache(Object key, Object value, SharedSessionContractImplementor session) {
        try {
            getCache().put(key, value);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new CacheException(e);
        } catch (Exception e) {
            if (LoggerFactory.getCacheLog().isErrorEnabled()) {
                LoggerFactory.getCacheLog().error("redis缓存存放失败");
            }
            throw new CacheException(e);
        }
    }

    @Override
    public boolean contains(Object key) {
        return getCache().get(key) != null;
    }

    @Override
    public void evictData() {
        try {
            getCache().clear();
        } catch (Exception e) {
            if (LoggerFactory.getCacheLog().isErrorEnabled()) {
                LoggerFactory.getCacheLog().error("redis缓存清空数据失败");
            }
            throw new CacheException(e);
        }
    }

    @Override
    public void evictData(Object key) {
        try {
            getCache().get(key);
        } catch (Exception e) {
            if (LoggerFactory.getCacheLog().isErrorEnabled()) {
                LoggerFactory.getCacheLog().error("redis缓存删除数据失败");
            }
            throw new CacheException(e);
        }
    }

    @Override
    public void release() {
        try {
            getCache().clear();
        } catch (Exception e) {
            if (e instanceof RedisConnectionFailureException) {
                LoggerFactory.getCacheLog().error("redis连接失败");
            } else {
                if (LoggerFactory.getCacheLog().isErrorEnabled()) {
                    LoggerFactory.getCacheLog().error("redis缓存删除数据失败");
                }
            }
            throw new CacheException(e);
        }
    }
}
