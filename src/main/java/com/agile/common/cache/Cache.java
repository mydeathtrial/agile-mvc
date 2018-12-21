package com.agile.common.cache;

import org.springframework.lang.Nullable;

/**
 * @author 佟盟 on 2018/9/6
 */
public interface Cache extends org.springframework.cache.Cache {
    void put(Object var1, @Nullable Object var2, int timeout);

    <T> T getCollection(Object o1, Class<T> o2);

    Long setNx(String key, String value);

    void expire(String lockName, int time);
}
