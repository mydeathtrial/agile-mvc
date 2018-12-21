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
    private static long maxEntriesLocalHeap;
    private static long maxEntriesLocalDisk;
    private static int timeToIdle;
    private static int timeToLive;
    private static int diskSpoolBufferSize;
    private static boolean blocking;
    private static String memoryStoreEvictionPolicy;
    private static boolean eternal;
    private static long diskExpiryThreadIntervalSeconds;

    public static long getMaxEntriesLocalHeap() {
        return maxEntriesLocalHeap;
    }

    public static void setMaxEntriesLocalHeap(long maxEntriesLocalHeap) {
        EhCacheProperties.maxEntriesLocalHeap = maxEntriesLocalHeap;
    }

    public static long getMaxEntriesLocalDisk() {
        return maxEntriesLocalDisk;
    }

    public static void setMaxEntriesLocalDisk(long maxEntriesLocalDisk) {
        EhCacheProperties.maxEntriesLocalDisk = maxEntriesLocalDisk;
    }

    public static int getTimeToIdle() {
        return timeToIdle;
    }

    public static void setTimeToIdle(int timeToIdle) {
        EhCacheProperties.timeToIdle = timeToIdle;
    }

    public static int getTimeToLive() {
        return timeToLive;
    }

    public static void setTimeToLive(int timeToLive) {
        EhCacheProperties.timeToLive = timeToLive;
    }

    public static int getDiskSpoolBufferSize() {
        return diskSpoolBufferSize;
    }

    public static void setDiskSpoolBufferSize(int diskSpoolBufferSize) {
        EhCacheProperties.diskSpoolBufferSize = diskSpoolBufferSize;
    }

    public static boolean isBlocking() {
        return blocking;
    }

    public static void setBlocking(boolean blocking) {
        EhCacheProperties.blocking = blocking;
    }

    public static String getMemoryStoreEvictionPolicy() {
        return memoryStoreEvictionPolicy;
    }

    public static void setMemoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
        EhCacheProperties.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
    }

    public static boolean isEternal() {
        return eternal;
    }

    public static void setEternal(boolean eternal) {
        EhCacheProperties.eternal = eternal;
    }

    public static long getDiskExpiryThreadIntervalSeconds() {
        return diskExpiryThreadIntervalSeconds;
    }

    public static void setDiskExpiryThreadIntervalSeconds(long diskExpiryThreadIntervalSeconds) {
        EhCacheProperties.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
    }
}
