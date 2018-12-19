package com.agile.common.properties;

import java.nio.charset.StandardCharsets;

/**
 * Created by 佟盟 on 2018/2/1
 */
public class FileConfigProperty {
    private static long maxUploadSize = 204800;
    private static String defaultEncoding = StandardCharsets.UTF_8.name();
    private static String includeFormat = "txt,excel";

    public long getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(long maxUploadSize) {
        FileConfigProperty.maxUploadSize = maxUploadSize;
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        FileConfigProperty.defaultEncoding = defaultEncoding;
    }

    public String getIncludeFormat() {
        return includeFormat;
    }

    public void setIncludeFormat(String includeFormat) {
        FileConfigProperty.includeFormat = includeFormat;
    }
}
