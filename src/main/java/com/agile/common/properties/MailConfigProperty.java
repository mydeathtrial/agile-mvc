package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.mail")
@Setter
@Getter
public class MailConfigProperty {
    private boolean enable;
    private String serverHost = "smtp.163.com";
    private int serverPort;
    private String serverDefaultFrom = "mydeathtrial@163.com";
    private String serverUsername = "mydeathtrial@163.com";
    private String serverPassword = "tongmeng19900905";
}
