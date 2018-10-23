package com.agile.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by 佟盟 on 2018/7/4
 */
public class VerificationCodeException extends AuthenticationException {
    public VerificationCodeException(String msg, Throwable t) {
        super(msg, t);
    }

    public VerificationCodeException(String msg) {
        super(msg);
    }
}
