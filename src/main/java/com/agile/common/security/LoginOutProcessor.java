package com.agile.common.security;

/**
 * @author 佟盟
 * 日期 2019/10/11 14:24
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public interface LoginOutProcessor {
    /**
     * 退出之前
     *
     * @param token 身份令牌
     */
    void before(String token);

    /**
     * 退出之后
     *
     * @param token 身份令牌
     */
    void after(String token);
}
