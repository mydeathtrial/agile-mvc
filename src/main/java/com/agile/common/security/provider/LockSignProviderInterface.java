package com.agile.common.security.provider;

import com.agile.common.security.LoginFilter;

/**
 * @author 佟盟
 * 日期 2020/6/9 21:27
 * 描述 登陆锁定钩子
 * @version 1.0
 * @since 1.0
 */
public interface LockSignProviderInterface {
    /**
     * 登陆锁定
     * @param errorSignInfo 错误登陆信息
     */
    void lock(LoginFilter.ErrorSignInfo errorSignInfo);
}
