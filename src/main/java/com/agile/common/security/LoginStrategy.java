package com.agile.common.security;

/**
 * @author 佟盟
 * 日期 2019/3/15 12:21
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public enum LoginStrategy {

    /**
     * 单例-禁止替换
     */
    SINGLETON,
    /**
     * 多例
     */
    MORE,
    /**
     * 单例-替换上一个用户
     */
    SINGLETON_REPLACE;


    public static final String LOGIN_STRATEGY_DIC = "LOGIN_STRATEGY_DIC";

    public static LoginStrategy coverLoginStrategy(int code) {
        LoginStrategy result;
        final int a = 1000, b = 2000, c = 3000;
        switch (code) {
            case a:
                result = SINGLETON_REPLACE;
                break;
            case b:
                result = MORE;
                break;
            case c:
                result = SINGLETON;
                break;
            default:
                result = SINGLETON;
        }
        return result;
    }
}
