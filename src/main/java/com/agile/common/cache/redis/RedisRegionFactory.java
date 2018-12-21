package com.agile.common.cache.redis;

import com.agile.common.config.RedisConfig;
import com.agile.common.factory.LoggerFactory;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.cache.spi.support.RegionFactoryTemplate;
import org.hibernate.cache.spi.support.RegionNameQualifier;
import org.hibernate.cache.spi.support.StorageAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 佟盟 on 2018/5/10
 */
public class RedisRegionFactory extends RegionFactoryTemplate {
    private static final AtomicInteger REFERENCE_COUNT = new AtomicInteger();
    private volatile RedisCacheManager cacheManager;
    private RedisCacheManager redisCacheManager;


    @Override
    protected DomainDataStorageAccess createDomainDataStorageAccess(
            DomainDataRegionConfig regionConfig,
            DomainDataRegionBuildingContext buildingContext) {
        return new StorageAccessImpl((RedisCache) getOrCreateCache(regionConfig.getRegionName(), buildingContext.getSessionFactory()));
    }

    @Override
    protected StorageAccess createQueryResultsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        return new StorageAccessImpl((RedisCache) getOrCreateCache(regionName, sessionFactory));
    }

    @Override
    protected StorageAccess createTimestampsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        return new StorageAccessImpl((RedisCache) getOrCreateCache(regionName, sessionFactory));
    }

    @Override
    protected void prepareForUse(SessionFactoryOptions settings, Map configValues) {
        synchronized (this) {
            this.cacheManager = (RedisCacheManager) resolveCacheManager();
            if (this.cacheManager == null) {
                String msg = "开启 Ehcache CacheManager 失败";
                if (LoggerFactory.getCacheLog().isErrorEnabled()) {
                    LoggerFactory.getCacheLog().error(msg);
                }
                throw new CacheException(msg);
            }
        }
    }

    @Override
    protected void releaseFromUse() {
        if (REFERENCE_COUNT.decrementAndGet() == 0) {
            cacheManager = null;
        }
    }

    private Cache getOrCreateCache(String unqualifiedRegionName, SessionFactoryImplementor sessionFactory) {
        verifyStarted();
        final String qualifiedRegionName = RegionNameQualifier.INSTANCE.qualify(
                unqualifiedRegionName,
                sessionFactory.getSessionFactoryOptions()
        );

        final Cache cache = cacheManager.getCache(qualifiedRegionName);
        if (cache == null) {
            throw new CacheException("未成功获取区域 [" + qualifiedRegionName + "]");
        }
        return cache;
    }

    private CacheManager resolveCacheManager() {
        return useExplicitCacheManager();
    }

    private CacheManager useExplicitCacheManager() {
        try {
            if (LoggerFactory.getCacheLog().isDebugEnabled()) {
                LoggerFactory.getCacheLog().debug("初始化Redis二级缓存区域");
            }
            initConnectionFactory();
            REFERENCE_COUNT.incrementAndGet();
            return redisCacheManager;
        } catch (Exception e) {
            if (LoggerFactory.getCacheLog().isDebugEnabled()) {
                LoggerFactory.getCacheLog().error("初始化Redis二级缓存区域失败");
                e.printStackTrace();
            }
            REFERENCE_COUNT.decrementAndGet();
            throw e;
        }
    }

    private void initConnectionFactory() {
        RedisConfig redisConfig = new RedisConfig();
        JedisPoolConfig jedisPoolConfig = redisConfig.redisPool();
        RedisConnectionFactory jedisConnectionFactory = redisConfig.jedisConnectionFactory(jedisPoolConfig);
        this.redisCacheManager = redisConfig.redisCacheManager(jedisConnectionFactory);
    }
}
