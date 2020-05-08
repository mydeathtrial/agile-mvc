package com.agile.common.exception;

import javax.servlet.ServletException;

/**
 * @author 佟盟
 * 日期 2019/5/15 11:13
 * 描述 登陆错误，并上锁
 * @version 1.0
 * @since 1.0
 */
public class LoginErrorLockException extends ServletException {
    public LoginErrorLockException(String valueOf) {
        super(valueOf);
    }

    public LoginErrorLockException() {
    }
}
