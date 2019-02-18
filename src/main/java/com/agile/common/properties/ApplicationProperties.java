package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/1/31 9:35
 * @Description TODO
 * @since 1.0
 */

@ConfigurationProperties(prefix = "agile")
@Setter
@Getter
public class ApplicationProperties {
    /**
     * 版本号
     */
    private String version;
    /**
     * 项目标题
     */
    private String title;
}
