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
@ConfigurationProperties(prefix = "agile.cors")
@Setter
@Getter
public class CorsFilterProperties {
    /**
     * header中的allowCredentials
     */
    private boolean allowCredentials = true;
    /**
     * header中的allowHeaders,允许请求携带的头部信息
     */
    private String allowHeaders = "Content-Type,X-CSRF-TOKEN,JSESSIONID,CODE_TOKEN,AGILE-TOKEN";
    /**
     * header中的allowMethods,允许访问的method请求方式
     */
    private String allowMethods = "GET,POST,PUT,DELETE,OPTIONS,JSONP";
    /**
     * header中的allowOrigin,设置为允许访问的域名,*为允许任何域名访问
     */
    private String allowOrigin = "*";
}
