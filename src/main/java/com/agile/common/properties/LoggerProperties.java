package com.agile.common.properties;

import com.agile.common.base.Constant;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/1/31 14:02
 * @Description TODO
 * @since 1.0
 */
@ConfigurationProperties(prefix = "agile.log")
@Setter
@Getter
public class LoggerProperties {
    private String packageUri = Constant.RegularAbout.SLASH;
    private TriggerType triggerType = TriggerType.TIME;
    private String triggerValue = "1";
    private Map<String, Level[]> packageName = new HashMap<>();

    /**
     * 类型
     */
    public enum TriggerType {
        /**
         * 时间
         */
        TIME,

        /**
         * 大小
         */
        SIZE
    }

}
