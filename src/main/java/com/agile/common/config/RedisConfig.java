package com.agile.common.config;

import com.agile.common.base.Constant;
import com.agile.common.cache.redis.RedisCacheManager;
import com.agile.common.properties.RedisConfigProperties;
import com.agile.common.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
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

/**
 * @author 佟盟 on 2017/10/8
 */
@Configuration
@EnableConfigurationProperties(value = {RedisConfigProperties.class})
@Conditional(RedisConfig.class)
public class RedisConfig implements Condition {

    @Autowired
    RedisConfigProperties redisConfigProperties;

    @Bean
    public JedisPoolConfig redisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisConfigProperties.getMaxIdle());
        jedisPoolConfig.setMinIdle(redisConfigProperties.getMinIdle());
        jedisPoolConfig.setMaxWaitMillis(redisConfigProperties.getMaxWaitMillis());
        jedisPoolConfig.setTestOnReturn(redisConfigProperties.isTestOnReturn());
        jedisPoolConfig.setTestOnBorrow(redisConfigProperties.isTestOnReturn());
        return jedisPoolConfig;
    }

    @Bean
    public RedisConnectionFactory jedisConnectionFactory(JedisPoolConfig redisPool) {
        String host = redisConfigProperties.getHost();
        String port = redisConfigProperties.getPort();

        if (host.contains(Constant.RegularAbout.COMMA)) {
            String[] hosts = host.split(Constant.RegularAbout.COMMA);
            String[] ports = port.split(Constant.RegularAbout.COMMA);

            RedisSentinelConfiguration config = new RedisSentinelConfiguration()
                    .master("master");
            for (int i = 0; i < hosts.length; i++) {
                try {
                    String currentHost = hosts[i];
                    int currentPost = Integer.parseInt(ports[i]);
                    config.sentinel(currentHost, currentPost);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return new JedisConnectionFactory(config, redisPool);
        } else {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisPool);
            jedisConnectionFactory.afterPropertiesSet();
            config.setPassword(RedisPassword.of(redisConfigProperties.getPass()));
            config.setHostName(host);
            config.setPort(Integer.parseInt(port));
            return new JedisConnectionFactory(config);
        }
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory jedisConnectionFactory) {
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
        defaultCacheConfig.entryTtl(Duration.ofSeconds(redisConfigProperties.getDuration()));
        //初始化RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String cacheProxy = PropertiesUtil.getProperty("agile.cache.proxy", "ehcache").toLowerCase();
        return "redis".equals(cacheProxy);
    }
}
