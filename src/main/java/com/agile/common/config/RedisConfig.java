package com.agile.common.config;

import com.agile.common.cache.redis.RedisCacheManager;
import com.agile.common.properties.RedisConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;
import java.time.Duration;
import java.util.List;

/**
 * Created by 佟盟 on 2017/10/8
 */
@Configuration
public class RedisConfig {

    @Bean
    public JedisPoolConfig redisPool(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(RedisConfigProperties.getMaxIdle());
        jedisPoolConfig.setMinIdle(RedisConfigProperties.getMinIdle());
        jedisPoolConfig.setMaxWaitMillis(RedisConfigProperties.getMaxWaitMillis());
        jedisPoolConfig.setTestOnReturn(RedisConfigProperties.isTestOnReturn());
        jedisPoolConfig.setTestOnBorrow(RedisConfigProperties.isTestOnReturn());
        return jedisPoolConfig;
    }

    @Bean
    public RedisConnectionFactory jedisConnectionFactory(JedisPoolConfig redisPool){
        List<String> hosts = RedisConfigProperties.getHost();
        List<Integer> ports = RedisConfigProperties.getPort();
        if(hosts.size()>1){
            RedisSentinelConfiguration config = new RedisSentinelConfiguration()
                    .master("master");
            for(int i = 0 ; i < hosts.size();i++){
                config.sentinel(hosts.get(i),ports.get(i));
            }
            return new JedisConnectionFactory(config,redisPool);
        }else{
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisPool);
            jedisConnectionFactory.afterPropertiesSet();
            config.setPassword(RedisPassword.of(RedisConfigProperties.getPass()));
            config.setHostName(hosts.get(0));
            config.setPort(ports.get(0));
            return new JedisConnectionFactory(config);
        }
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory jedisConnectionFactory){
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(jdkSerializationRedisSerializer);
        template.setValueSerializer(jdkSerializationRedisSerializer);
        template.setHashKeySerializer(jdkSerializationRedisSerializer);
        template.setHashValueSerializer(jdkSerializationRedisSerializer);
        template.afterPropertiesSet();
        template.setEnableTransactionSupport(true);
        return template;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory jedisConnectionFactory) {
        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(jedisConnectionFactory);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        //设置默认超过期时间是30秒
        defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
        //初始化RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }
}
