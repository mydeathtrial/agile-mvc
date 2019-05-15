package com.agile.common.properties;

import com.agile.common.base.Constant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.security")
@Setter
@Getter
public class SecurityProperties {
    /**
     * 开关
     */
    private boolean enable = true;
    /**
     * 排除的地址
     */
    private String excludeUrl = "";
    /**
     * 登陆地址
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
     * 登陆账号表单名
     */
    private String loginUsername = "username";
    /**
     * 登陆密码表单名
     */
    private String loginPassword = "password";

    /**
     * token类型
     */
    private TokenType tokenType = TokenType.EASY;

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

    /**
     * 登陆失败次数
     */
    private int loginErrorCount = Constant.NumberAbout.FIVE;

    /**
     * 登陆失败锁定时间
     */
    private Duration loginLockTime = Duration.ofMinutes(Constant.NumberAbout.FIVE);

    /**
     * 登陆失败信息超时
     */
    private Duration loginErrorTimeout = Duration.ofMinutes(Constant.NumberAbout.TWO);

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
}
