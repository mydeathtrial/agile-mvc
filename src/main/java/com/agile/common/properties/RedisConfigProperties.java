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
    /**
     * 开关
     */
    private boolean enable;
    /**
     * 密码
     */
    private String pass;
    private int maxIdle;
    private int minIdle;
    private int maxWaitMillis;
    private boolean testOnReturn;
    private boolean testOnBorrow;
    /**
     * ip
     */
    private String host;
    /**
     * 端口
     */
    private String port;
    private long duration;
}
