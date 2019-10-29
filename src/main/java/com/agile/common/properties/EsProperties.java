package com.agile.common.properties;

import com.agile.common.util.ObjectUtil;
import com.idss.common.datafactory.utils.ESConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
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
     * 日期格式
     */
    private String indexDateFormat;
    /**
     * 创建时间字段
     */
    private String timeField;
    /**
     * 主键字段
     */
    private String idField;
    private String rawMsgFiled;
    private int scrollTimeValue;
    private int scrollSetSize;
    private String defaultEsKey;
    private int maxSize;

    /**
     * ES配置
     */
    private Map<String, ESConfig> config = new LinkedHashMap<>();

    public EsProperties initDefault() {
        for (ESConfig con : config.values()) {
            ObjectUtil.copyProperties(this, con, ObjectUtil.Compare.DIFF_SOURCE_NOT_NULL_AND_TARGET_DEFAULT);
        }
        return this;
    }
}
