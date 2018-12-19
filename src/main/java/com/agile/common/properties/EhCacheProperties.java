package com.agile.common.properties;

import com.agile.common.annotation.Properties;

/**
 * 描述：
 * <p>创建时间：2018/12/19<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Properties(prefix = "agile.ehcache")
public class EhCacheProperties {
    private static int maxEntriesLocalHeap;
    private static long timeToIdleSeconds;
    private static long timeToLiveSeconds;
    private static long diskExpiryThreadIntervalSeconds;

    public static int getMaxEntriesLocalHeap() {
        return maxEntriesLocalHeap;
    }

    public static void setMaxEntriesLocalHeap(int maxEntriesLocalHeap) {
        EhCacheProperties.maxEntriesLocalHeap = maxEntriesLocalHeap;
    }

    public static long getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public static void setTimeToIdleSeconds(long timeToIdleSeconds) {
        EhCacheProperties.timeToIdleSeconds = timeToIdleSeconds;
    }

    public static long getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public static void setTimeToLiveSeconds(long timeToLiveSeconds) {
        EhCacheProperties.timeToLiveSeconds = timeToLiveSeconds;
    }

    public static long getDiskExpiryThreadIntervalSeconds() {
        return diskExpiryThreadIntervalSeconds;
    }

    public static void setDiskExpiryThreadIntervalSeconds(long diskExpiryThreadIntervalSeconds) {
        EhCacheProperties.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
    }
}
