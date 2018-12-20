package com.agile.common.properties;

import com.agile.common.annotation.Properties;

/**
 * Created by 佟盟 on 2018/1/11
 */
@Properties(prefix = "agile.kaptcha")
public class KaptchaConfigProperties {
    private static boolean enable;
    private static String url = "/code";
    private static String border = "no";
    private static String borderColor = "black";
    private static String textproducerFontColor = "black";
    private static String textproducerFontSize = "40";
    private static String imageWidth = "125";
    private static String imageHeight = "45";
    private static String textproducerCharLength = "4";
    private static String textproducerFontNames = "微软雅黑";
    private static String text;
    private static String key = "V-CODE";
    private static int liveTime;

    public static boolean isEnable() {
        return enable;
    }

    public static void setEnable(boolean enable) {
        KaptchaConfigProperties.enable = enable;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        KaptchaConfigProperties.url = url;
    }

    public static String getBorder() {
        return border;
    }

    public static void setBorder(String border) {
        KaptchaConfigProperties.border = border;
    }

    public static String getBorderColor() {
        return borderColor;
    }

    public static void setBorderColor(String borderColor) {
        KaptchaConfigProperties.borderColor = borderColor;
    }

    public static String getTextproducerFontColor() {
        return textproducerFontColor;
    }

    public static void setTextproducerFontColor(String textproducerFontColor) {
        KaptchaConfigProperties.textproducerFontColor = textproducerFontColor;
    }

    public static String getTextproducerFontSize() {
        return textproducerFontSize;
    }

    public static void setTextproducerFontSize(String textproducerFontSize) {
        KaptchaConfigProperties.textproducerFontSize = textproducerFontSize;
    }

    public static String getImageWidth() {
        return imageWidth;
    }

    public static void setImageWidth(String imageWidth) {
        KaptchaConfigProperties.imageWidth = imageWidth;
    }

    public static String getImageHeight() {
        return imageHeight;
    }

    public static void setImageHeight(String imageHeight) {
        KaptchaConfigProperties.imageHeight = imageHeight;
    }

    public static String getTextproducerCharLength() {
        return textproducerCharLength;
    }

    public static void setTextproducerCharLength(String textproducerCharLength) {
        KaptchaConfigProperties.textproducerCharLength = textproducerCharLength;
    }

    public static String getTextproducerFontNames() {
        return textproducerFontNames;
    }

    public static void setTextproducerFontNames(String textproducerFontNames) {
        KaptchaConfigProperties.textproducerFontNames = textproducerFontNames;
    }

    public static String getText() {
        return text;
    }

    public static void setText(String text) {
        KaptchaConfigProperties.text = text;
    }

    public static String getKey() {
        return key;
    }

    public static void setKey(String key) {
        KaptchaConfigProperties.key = key;
    }

    public static int getLiveTime() {
        return liveTime;
    }

    public static void setLiveTime(int liveTime) {
        KaptchaConfigProperties.liveTime = liveTime;
    }
}
