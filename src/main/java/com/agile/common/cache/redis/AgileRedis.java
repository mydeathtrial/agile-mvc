package com.agile.common.cache.redis;

import com.agile.common.cache.AbstractAgileCache;
import com.google.common.collect.Maps;
import org.springframework.cache.support.NullValue;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 佟盟
 * 日期 2019/7/23 18:36
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class AgileRedis extends AbstractAgileCache {
    private static final byte[] BINARY_NULL_VALUE = RedisSerializer.java().serialize(NullValue.INSTANCE);

    private RedisConnectionFactory redisConnectionFactory;
    private final RedisCacheConfiguration cacheConfig;
    private final ConversionService conversionService;
    private final String name;

    private final Duration sleepTime = Duration.ZERO;

    AgileRedis(RedisCache cache, RedisConnectionFactory redisConnectionFactory) {
        super(cache);
        this.name = cache.getName();
        this.redisConnectionFactory = redisConnectionFactory;
        this.cacheConfig = cache.getCacheConfiguration();
        this.conversionService = cacheConfig.getConversionService();
    }

    @Override
    public void put(Object key, Object value, Duration timeout) {
        execute(name, connection -> connection.set(createAndConvertCacheKey(key), serializeCacheValue(value), Expiration.seconds(timeout.getSeconds()), RedisStringCommands.SetOption.UPSERT));
    }

    @Override
    public boolean containKey(Object key) {
        return execute(name, connection -> connection.exists(createAndConvertCacheKey(key)));
    }

    @Override
    public void addToMap(Object mapKey, Object key, Object value) {
        executeConsumer(name, connection -> connection.hSet(createAndConvertCacheKey(mapKey), serializeCacheKey(convertKey(key)), serializeCacheValue(value)));
    }

    @Override
    public Object getFromMap(Object mapKey, Object key) {
        List<byte[]> list = execute(name, connection -> connection.hMGet(createAndConvertCacheKey(mapKey), serializeCacheKey(convertKey(key))));
        if (list == null || list.get(0) == null) {
            return null;
        }

        return deserializeCacheValue(list.get(0));
    }

    @Override
    public void removeFromMap(Object mapKey, Object key) {
        executeConsumer(name, connection -> connection.hDel(createAndConvertCacheKey(mapKey), serializeCacheKey(convertKey(key))));
    }

    @Override
    public void addToList(Object listKey, Object node) {
        executeConsumer(name, connection -> connection.rPush(createAndConvertCacheKey(listKey), serializeCacheValue(node)));
    }

    @Override
    public Object getFromList(Object listKey, int index) {
        List<byte[]> list = execute(name, connection -> connection.lRange(createAndConvertCacheKey(listKey), index, index + 1));
        if (list == null) {
            return null;
        }
        return deserializeCacheValue(list.get(0));
    }

    @Override
    public void removeFromList(Object listKey, int index) {
        executeConsumer(name, connection -> connection.lRem(createAndConvertCacheKey(listKey), 0, serializeCacheValue(getFromList(listKey, index))));
    }

    @Override
    public void addToSet(Object setKey, Object node) {
        executeConsumer(name, connection -> connection.sAdd(createAndConvertCacheKey(setKey), serializeCacheValue(node)));
    }

    @Override
    public void removeFromSet(Object setKey, Object node) {
        executeConsumer(name, connection -> connection.sRem(createAndConvertCacheKey(setKey), serializeCacheValue(node)));
    }

    @Override
    public boolean lock(Object lock) {
        return execute(name, connection -> doLock(lock, connection));
    }

    @Override
    public boolean lock(Object lock, Duration timeout) {
        boolean isLock = execute(name, connection -> doLock(lock, connection));
        if (isLock) {
            execute(name, connection -> connection.expire(createAndConvertCacheKey(createCacheLockKey(lock)), timeout.getSeconds()));
        }
        return isLock;
    }

    @Override
    public void unlock(Object lock) {
        executeLockFree(connection -> doUnlock(lock, connection));
    }

    @Override
    public void unlock(Object lock, Duration timeout) {
        execute(name, connection -> connection.expire(createAndConvertCacheKey(createCacheLockKey(lock)), timeout.getSeconds()));
    }

    private byte[] createAndConvertCacheKey(Object key) {
        return serializeCacheKey(createCacheKey(key));
    }

    private byte[] serializeCacheKey(String cacheKey) {
        return ByteUtils.getBytes(cacheConfig.getKeySerializationPair().write(cacheKey));
    }

    private String createCacheKey(Object key) {

        String convertedKey = convertKey(key);

        if (!cacheConfig.usePrefix()) {
            return convertedKey;
        }

        return prefixCacheKey(convertedKey);
    }

    private String prefixCacheKey(String key) {

        // allow contextual cache names by computing the key prefix on every call.
        return cacheConfig.getKeyPrefixFor(name) + key;
    }


    private String convertKey(Object key) {

        TypeDescriptor source = TypeDescriptor.forObject(key);
        if (conversionService.canConvert(source, TypeDescriptor.valueOf(String.class))) {
            return conversionService.convert(key, String.class);
        }

        Method toString = ReflectionUtils.findMethod(key.getClass(), "toString");

        if (toString != null && !Object.class.equals(toString.getDeclaringClass())) {
            return key.toString();
        }

        throw new IllegalStateException(
                String.format("Cannot convert %s to String. Register a Converter or override toString().", source));
    }

    private byte[] serializeCacheValue(Object value) {

        if (isAllowNullValues() && value instanceof NullValue) {
            return BINARY_NULL_VALUE;
        }

        return ByteUtils.getBytes(cacheConfig.getValueSerializationPair().write(value));
    }

    protected Object deserializeCacheValue(byte[] value) {

        if (isAllowNullValues() && ObjectUtils.nullSafeEquals(value, BINARY_NULL_VALUE)) {
            return NullValue.INSTANCE;
        }

        return cacheConfig.getValueSerializationPair().read(ByteBuffer.wrap(value));
    }

    protected Object deserializeCacheKey(byte[] value) {

        if (isAllowNullValues() && ObjectUtils.nullSafeEquals(value, BINARY_NULL_VALUE)) {
            return NullValue.INSTANCE;
        }

        return cacheConfig.getKeySerializationPair().read(ByteBuffer.wrap(value));
    }

    private boolean isAllowNullValues() {
        return ((RedisCache) cache).isAllowNullValues();
    }

    private <T> T execute(String name, Function<RedisConnection, T> callback) {

        RedisConnection connection = redisConnectionFactory.getConnection();
        try {

            checkAndPotentiallyWaitUntilUnlocked(name, connection);
            return callback.apply(connection);
        } finally {
            connection.close();
        }
    }

    private void executeConsumer(String name, Consumer<RedisConnection> callback) {

        RedisConnection connection = redisConnectionFactory.getConnection();
        try {

            checkAndPotentiallyWaitUntilUnlocked(name, connection);
            callback.accept(connection);
        } finally {
            connection.close();
        }
    }

    private void checkAndPotentiallyWaitUntilUnlocked(String name, RedisConnection connection) {

        if (!isLockingCacheWriter()) {
            return;
        }

        try {

            while (doCheckLock(name, connection)) {
                Thread.sleep(sleepTime.toMillis());
            }
        } catch (InterruptedException ex) {

            // Re-interrupt current thread, to allow other participants to react.
            Thread.currentThread().interrupt();

            throw new PessimisticLockingFailureException(String.format("Interrupted while waiting to unlock cache %s", name),
                    ex);
        }
    }

    private boolean isLockingCacheWriter() {
        return !sleepTime.isZero() && !sleepTime.isNegative();
    }

    private boolean doCheckLock(Object name, RedisConnection connection) {
        return connection.exists(createAndConvertCacheKey(createCacheLockKey(name)));
    }

    private static byte[] toBytes(String name) {
        return name.getBytes(StandardCharsets.UTF_8);
    }

    private static String createCacheLockKey(Object name) {
        return name.toString() + "~lock";
    }

    private Boolean doLock(Object name, RedisConnection connection) {
        return connection.setNX(createAndConvertCacheKey(createCacheLockKey(name)), new byte[0]);
    }

    private Long doUnlock(Object name, RedisConnection connection) {
        return connection.del(createAndConvertCacheKey(createCacheLockKey(name)));
    }

    private void executeLockFree(Consumer<RedisConnection> callback) {

        RedisConnection connection = redisConnectionFactory.getConnection();

        try {
            callback.accept(connection);
        } finally {
            connection.close();
        }
    }

    @Override
    public void put(Object key, Object value) {
        if (Map.class.isAssignableFrom(value.getClass())) {
            Map<byte[], byte[]> map = Maps.newHashMapWithExpectedSize(((Map) value).size());
            ((Map<Object, Object>) value).forEach((eKey, eValue) -> map.put(serializeCacheKey(convertKey(eKey)), serializeCacheValue(eValue)));
            try {
                executeConsumer(name, connection -> connection.hMSet(createAndConvertCacheKey(key), map));
            } catch (RedisSystemException e) {
                map.forEach((ekey, eValue) -> {
                    addToMap(key, ekey, eValue);
                });
            }

        } else if (List.class.isAssignableFrom(value.getClass())) {
            int size = ((List) value).size();
            byte[][] arr = new byte[size][];
            List<byte[]> result = ((List<Object>) value).stream().map(this::serializeCacheValue).collect(Collectors.toList());
            byte[][] list = result.toArray(arr);
            execute(name, connection -> connection.rPush(createAndConvertCacheKey(key), list));
        } else if (Set.class.isAssignableFrom(value.getClass())) {
            int size = ((Set) value).size();
            byte[][] arr = new byte[size][];
            Set<byte[]> result = ((Set<Object>) value).stream().map(this::serializeCacheValue).collect(Collectors.toSet());
            byte[][] set = result.toArray(arr);
            execute(name, connection -> connection.sAdd(createAndConvertCacheKey(key), set));
        } else {
            super.put(key, value);
        }
    }

    @Override
    public <T> T get(Object key, Class<T> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            Map<byte[], byte[]> map = execute(name, connection -> connection.hGetAll(createAndConvertCacheKey(key)));
            HashMap<Object, Object> res = Maps.newHashMapWithExpectedSize(map.size());
            map.forEach((eK, eV) -> {
                res.put(deserializeCacheKey(eK), deserializeCacheValue(eV));
            });
            return (T) res;
        } else if (List.class.isAssignableFrom(clazz)) {
            List<byte[]> list = execute(name, connection -> connection.lRange(createAndConvertCacheKey(key), 0, -1));
            return (T) list.stream().map(node -> deserializeCacheValue(node)).collect(Collectors.toList());
        } else if (Set.class.isAssignableFrom(clazz)) {
            Set<byte[]> set = execute(name, connection -> connection.sMembers(createAndConvertCacheKey(key)));
            return (T) set.stream().map(node -> deserializeCacheValue(node)).collect(Collectors.toSet());
        } else {
            return super.get(key, clazz);
        }
    }

    @Override
    public Object getNativeCache() {
        return redisConnectionFactory.getConnection();
    }


}
