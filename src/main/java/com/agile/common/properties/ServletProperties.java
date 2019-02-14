package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/1/31 15:21
 * @Description TODO
 * @since 1.0
 */
@ConfigurationProperties(prefix = "agile.servlet")
@Setter
@Getter
public class ServletProperties {
    private boolean allowCredentials = true;
    private String allowHeaders = "Content-Type,X-CSRF-TOKEN,JSESSIONID";
    private String allowMethods = "GET,POST,PUT,DELETE,OPTIONS,JSONP";
    private String allowOrigin = "*";
}
