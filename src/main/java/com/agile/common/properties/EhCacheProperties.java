package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.ehcache")
@Setter
@Getter
public class EhCacheProperties {
    /**
     * 开关
     */
    private boolean enable;
    /**
     * 最大本地实体堆
     */
    private long maxEntriesLocalHeap;
    /**
     * 最大本地硬盘存储
     */
    private long maxEntriesLocalDisk;
    /**
     * 元素最大闲置时间
     */
    private int timeToIdle;
    /**
     * 元素最大生存时间
     */
    private int timeToLive;
    /**
     * 磁盘线轴
     */
    private int diskSpoolBufferSize;
    /**
     * 阻塞
     */
    private boolean blocking;
    /**
     * //清理机制：LRU最近最少使用 FIFO先进先出 LFU较少使用
     */
    private String memoryStoreEvictionPolicy;
    /**
     * 元素是否永久缓存
     */
    private boolean eternal;
    /**
     * 缓存清理时间(默认120秒)
     */
    private long diskExpiryThreadIntervalSeconds;
}
