package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 佟盟
 * 日期 2019/8/7 14:53
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@ConfigurationProperties(prefix = "agile.dictionary")
@Setter
@Getter
public class DictionaryProperties {
    /**
     * 开关
     */
    private boolean enable;
}
