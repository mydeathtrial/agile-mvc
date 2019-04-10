package com.agile.common.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author 佟盟
 * 日期 2019/3/15 12:15
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public interface CustomerUserDetails extends UserDetails {
    /**
     * 登陆策略
     *
     * @return 用户登陆策略
     */
    LoginStrategy getLoginStrategy();

    /**
     * 明文用户名
     *
     * @return 用户名
     */
    String getName();
}
