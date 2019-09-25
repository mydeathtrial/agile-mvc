package com.agile.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author 佟盟
 * 日期 2019/5/15 11:13
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class LoginErrorLockException extends AuthenticationException {
    public LoginErrorLockException(String msg, Throwable t) {
        super(msg, t);
    }

    public LoginErrorLockException(String msg) {
        super(msg);
    }
}
