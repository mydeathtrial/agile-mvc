package com.agile.common.security;

import java.util.Date;

/**
 * @author 佟盟
 * 日期 2019/3/15 13:07
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public interface CustomerLoginInfo {
    /**
     * 账号
     *
     * @return 账号
     */
    String getUserName();

    /**
     * 登陆时间
     *
     * @return 登陆时间
     */
    Date getLoginTime();

    /**
     * 退出时间
     *
     * @return 退出时间
     */
    Date getLogoutTime();

    /**
     * 登陆ip
     *
     * @return 登陆ip
     */
    String getLoginIp();

    /**
     * 令牌
     *
     * @return 令牌
     */
    String getToken();

}
