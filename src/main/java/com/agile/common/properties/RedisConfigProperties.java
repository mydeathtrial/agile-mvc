package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.redis")
@Setter
@Getter
public class RedisConfigProperties {
    private boolean enable;
    private String pass;
    private int maxIdle;
    private int minIdle;
    private int maxWaitMillis;
    private boolean testOnReturn;
    private boolean testOnBorrow;
    private String host;
    private String port;
    private long duration;
}
