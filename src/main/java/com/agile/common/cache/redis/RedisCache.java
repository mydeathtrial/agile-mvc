package com.agile.common.cache.redis;

import com.agile.common.cache.Cache;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.ObjectUtil;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 佟盟 on 2018/9/6
 */
public class RedisCache extends org.springframework.data.redis.cache.RedisCache implements Cache {
    private String name;
    private static RedisTemplate redisTemplate;
    private static Jedis jedis;

    protected RedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
        this.name = name;
    }

    @Override
    public void put(Object key, Object value, int timeout) {
        getNativeCache().put(name, this.serializeCacheKey(this.createCacheKey(key)), this.serializeCacheValue(value), Duration.ofSeconds(timeout));
    }

    @Override
    public void put(Object key, Object value) {
        if (value instanceof Map) {
            getRedisTemplate().opsForHash().putAll(key, (Map) value);
        } else if (value instanceof Set) {
            SetOperations set = getRedisTemplate().opsForSet();
            Iterator it = ((Set) value).iterator();
            int i = 0;
            while (it.hasNext()) {
                Object o = it.next();
                set.add("key" + (i++), o);
            }
        } else if (value instanceof List) {
            getRedisTemplate().opsForList().rightPush(key, value);
        } else {
            super.put(key, value);
        }
    }

    @Override
    public Long setNx(String key, String value) {
        return getJedis().setnx(key, value);
    }

    @Override
    public void expire(String name, int time) {
        getJedis().expire(name, time);
    }

    @Override
    public <T> T getCollection(Object o1, Class<T> o2) {
        try {
            if (o2 == Map.class) {
                return (T) getRedisTemplate().opsForHash().entries(o1);
            } else if (o2 == Set.class) {
                return (T) getRedisTemplate().opsForSet().members(o1);
            } else if (o2 == List.class) {
                return (T) getRedisTemplate().opsForList().rightPop(o1);
            }
            return (T) get(o1).get();
        } catch (Exception e) {
            return null;
        }
    }

    private static RedisTemplate getRedisTemplate() {
        if (ObjectUtil.isEmpty(redisTemplate)) {
            redisTemplate = FactoryUtil.getBean(RedisTemplate.class);
        }
        return redisTemplate;
    }

    public static Jedis getJedis() {
        if (ObjectUtil.isEmpty(jedis)) {
            JedisConnectionFactory jedisConnectionFactory = FactoryUtil.getBean(JedisConnectionFactory.class);
            if (!ObjectUtil.isEmpty(jedisConnectionFactory)) {
                jedis = (Jedis) jedisConnectionFactory.getConnection().getNativeConnection();
            }
        }
        return jedis;
    }
}
