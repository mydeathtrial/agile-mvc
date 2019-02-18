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
    /**
     * 打印日志到某个目录下
     */
    private String packageUri = Constant.RegularAbout.SLASH;
    /**
     * 日志打包依据
     */
    private TriggerType triggerType = TriggerType.TIME;
    /**
     * 日志打包依据值
     */
    private String triggerValue = "1";
    /**
     * 打印某个源码包的日志
     */
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
