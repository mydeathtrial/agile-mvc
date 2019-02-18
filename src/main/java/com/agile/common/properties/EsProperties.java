package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
    private String clusterName;
    private String clusterNodes;
    private String clusterHosts;
    private int poolSize;
    private String indexDateFormat;
    private String timeField;
    private String idField;
    private String rawMsgFiled;
    private int scrollTimeValue;
    private int scrollSetSize;
}
