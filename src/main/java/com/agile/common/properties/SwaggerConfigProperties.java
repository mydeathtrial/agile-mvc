package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author by 佟盟 on 2018/11/23
 */
@ConfigurationProperties(prefix = "agile.swagger")
@Setter
@Getter
public class SwaggerConfigProperties {
    private boolean enable;
    private String title;
    private String description;
    private String termsOfServiceUrl;
    private String version;
}
