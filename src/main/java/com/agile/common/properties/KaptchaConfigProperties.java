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
    /**
     * 开关
     */
    private boolean enable;
    /**
     * 验证码访问地址
     */
    private String url = "/code";
    /**
     * 是否有边
     */
    private String border = "no";
    /**
     * 边框颜色
     */
    private String borderColor = "black";
    /**
     * 文字颜色
     */
    private String textproducerFontColor = "black";
    /**
     * 文字大小
     */
    private String textproducerFontSize = "40";
    /**
     * 图片宽度
     */
    private String imageWidth = "125";
    /**
     * 图片高度
     */
    private String imageHeight = "45";
    /**
     * 字数
     */
    private String textproducerCharLength = "4";
    /**
     * 字体
     */
    private String textproducerFontNames = "微软雅黑";
    /**
     * 边框厚度
     */
    private String thickness = "1";
    /**
     * 依据的文字集
     */
    private String text;
    /**
     * 传输的头部信息
     */
    private String tokenHeader = "V-CODE";
    private int liveTime;
}
