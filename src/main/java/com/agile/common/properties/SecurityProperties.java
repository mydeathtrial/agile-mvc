package com.agile.common.properties;

import com.agile.common.annotation.Properties;

/**
 * Created by 佟盟 on 2018/2/1
 */
@Properties(prefix = "agile.security")
public class SecurityProperties {
    private static boolean enable = true;
    private static String excludeUrl = "";
    private static String loginUrl = "/login";
    private static String loginOutUrl = "/logout";
    private static String verificationCode = "verification";
    private static String tokenKey = "23617641641";
    private static int tokenTimeout;
    private static String tokenHeader = "AGILE_TOKEN";
    private static String loginUsername = "username";
    private static String loginPassword = "password";

    public static boolean isEnable() {
        return enable;
    }

    public static void setEnable(boolean enable) {
        SecurityProperties.enable = enable;
    }

    public static String getLoginUrl() {
        return loginUrl;
    }

    public static void setLoginUrl(String loginUrl) {
        SecurityProperties.loginUrl = loginUrl;
    }

    public static String getLoginOutUrl() {
        return loginOutUrl;
    }

    public static void setLoginOutUrl(String loginOutUrl) {
        SecurityProperties.loginOutUrl = loginOutUrl;
    }

    public static String getVerificationCode() {
        return verificationCode;
    }

    public static void setVerificationCode(String verificationCode) {
        SecurityProperties.verificationCode = verificationCode;
    }

    public static String getTokenKey() {
        return tokenKey;
    }

    public static void setTokenKey(String tokenKey) {
        SecurityProperties.tokenKey = tokenKey;
    }

    public static int getTokenTimeout() {
        return tokenTimeout;
    }

    public static void setTokenTimeout(int tokenTimeout) {
        SecurityProperties.tokenTimeout = tokenTimeout;
    }

    public static String getTokenHeader() {
        return tokenHeader;
    }

    public static void setTokenHeader(String tokenHeader) {
        SecurityProperties.tokenHeader = tokenHeader;
    }

    public static String getLoginUsername() {
        return loginUsername;
    }

    public static void setLoginUsername(String loginUsername) {
        SecurityProperties.loginUsername = loginUsername;
    }

    public static String getLoginPassword() {
        return loginPassword;
    }

    public static void setLoginPassword(String loginPassword) {
        SecurityProperties.loginPassword = loginPassword;
    }

    public static String getExcludeUrl() {
        return excludeUrl;
    }

    public static void setExcludeUrl(String excludeUrl) {
        SecurityProperties.excludeUrl = excludeUrl;
    }
}
