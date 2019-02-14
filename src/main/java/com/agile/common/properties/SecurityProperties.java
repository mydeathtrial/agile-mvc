package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.security")
@Setter
@Getter
public class SecurityProperties {
    private boolean enable = true;
    private String excludeUrl = "";
    private String loginUrl = "/login";
    private String loginOutUrl = "/logout";
    private String verificationCode = "verification";
    private String tokenKey = "23617641641";
    private int tokenTimeout;
    private String tokenHeader = "AGILE_TOKEN";
    private String loginUsername = "username";
    private String loginPassword = "password";
}
