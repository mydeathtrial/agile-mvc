package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.task")
@Setter
@Getter
public class TaskProperties {
    /**
     * 定时任务开关
     */
    private boolean enable = false;
}
