package com.agile.common.properties;

import com.agile.common.base.Constant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.time.Duration;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.security")
@Setter
@Getter
public class SecurityProperties implements Serializable {
    /**
     * 开关
     */
    private boolean enable = true;
    /**
     * 排除的地址
     */
    private String excludeUrl = "";
    /**
     * 登录地址
     */
    private String loginUrl = "/login";
    /**
     * 登出地址
     */
    private String loginOutUrl = "/logout";
    /**
     * 验证码
     */
    private String verificationCode = "verification";
    /**
     * token密钥
     */
    private String tokenSecret = "23617641641";
    /**
     * token超时时间
     */
    private Duration tokenTimeout = Duration.ofMinutes(Constant.NumberAbout.TEN * Constant.NumberAbout.THREE);
    /**
     * token传递header名
     */
    private String tokenHeader = "AGILE_TOKEN";
    /**
     * 登录账号表单名
     */
    private String loginUsername = "username";
    /**
     * 登录密码表单名
     */
    private String loginPassword = "password";

    /**
     * token类型
     */
    private TokenType tokenType = TokenType.EASY;

    /**
     * Token级别
     */
    public enum TokenType {
        /**
         * 容易
         */
        EASY,
        /**
         * 难
         */
        DIFFICULT
    }

    /**
     * 密码
     */
    private Password password = new Password();

    /**
     * 登陆
     */
    private Sign sign = new Sign();

    /**
     * 密码
     */
    @Data
    public static class Password implements Serializable {
        /**
         * 密码最低强度
         */
        private float strength = Constant.NumberAbout.FIVE;
        /**
         * 密码有效期
         */
        private Duration duration = Duration.ofDays(Constant.NumberAbout.THIRTY_ONE);
        /**
         * 过期是否锁定
         */
        private boolean lockForExpiration = true;
        /**
         * 密钥
         */
        private String aesKey = "idssinsightkey01";

        /**
         * 偏移量
         */
        private String aesOffset = "3612213421341234";

        /**
         * 算法模式
         */
        private String algorithmModel = "AES/CBC/PKCS5Padding";
    }

    /**
     * 登陆
     */
    @Data
    public static class Sign implements Serializable {
        /**
         * 最大登录失败次数
         */
        private int maxErrorCount = Constant.NumberAbout.FIVE;
        /**
         * 登录失败锁定时间
         */
        private Duration errorSignLockTime = Duration.ofMinutes(Constant.NumberAbout.TWO);
        /**
         * 登录失败计算超时
         */
        private Duration errorSignCountTimeout = Duration.ofMinutes(Constant.NumberAbout.TWO);
    }
}
