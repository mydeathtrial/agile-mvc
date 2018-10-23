package com.agile.common.properties;

import com.agile.common.annotation.Properties;

/**
 * Created by 佟盟 on 2018/2/1
 */
@Properties(prefix = "agile")
public class SpringMVCProperties {
    private static FileConfigProperty upload;

    public static FileConfigProperty getUpload() {
        return upload;
    }

    public static void setUpload(FileConfigProperty upload) {
        SpringMVCProperties.upload = upload;
    }
}
