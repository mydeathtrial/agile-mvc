package com.agile.common.properties;

import com.agile.common.annotation.Properties;

/**
 * Created by 佟盟 on 2018/11/23
 */
@Properties(prefix = "agile.swagger")
public class SwaggerConfigProperties {
    private static String title;
    private static String description;
    private static String termsOfServiceUrl;
    private static String version;

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        SwaggerConfigProperties.title = title;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        SwaggerConfigProperties.description = description;
    }

    public static String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public static void setTermsOfServiceUrl(String termsOfServiceUrl) {
        SwaggerConfigProperties.termsOfServiceUrl = termsOfServiceUrl;
    }

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        SwaggerConfigProperties.version = version;
    }
}
