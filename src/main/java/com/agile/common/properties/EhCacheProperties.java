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
    private boolean enable;
    private long maxEntriesLocalHeap;
    private long maxEntriesLocalDisk;
    private int timeToIdle;
    private int timeToLive;
    private int diskSpoolBufferSize;
    private boolean blocking;
    private String memoryStoreEvictionPolicy;
    private boolean eternal;
    private long diskExpiryThreadIntervalSeconds;
}
