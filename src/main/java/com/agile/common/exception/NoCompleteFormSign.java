package com.agile.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author 佟盟 on 2018/7/4
 */
public class NoCompleteFormSign extends AuthenticationException {
    public NoCompleteFormSign(String msg, Throwable t) {
        super(msg, t);
    }

    public NoCompleteFormSign(String msg) {
        super(msg);
    }

    public NoCompleteFormSign() {
        super(null);
    }
}
