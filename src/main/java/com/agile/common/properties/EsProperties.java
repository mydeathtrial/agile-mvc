package com.agile.common.properties;

import com.idss.common.datafactory.utils.ESConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.elasticsearch")
@Setter
@Getter
public class EsProperties {
    /**
     * 开关
     */
    private boolean enable;

    /**
     * ES配置
     */
    private Map<String, ESConfig> config = new HashMap<>();
}
