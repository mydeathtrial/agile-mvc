package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.kaptcha")
@Setter
@Getter
public class KaptchaConfigProperties {
    private boolean enable;
    private String url = "/code";
    private String border = "no";
    private String borderColor = "black";
    private String textproducerFontColor = "black";
    private String textproducerFontSize = "40";
    private String imageWidth = "125";
    private String imageHeight = "45";
    private String textproducerCharLength = "4";
    private String textproducerFontNames = "微软雅黑";
    private String text;
    private String tokenHeader = "V-CODE";
    private int liveTime;
}
