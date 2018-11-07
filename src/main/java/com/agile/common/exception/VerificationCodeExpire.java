package com.agile.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by 佟盟 on 2018/9/6
 */
public class VerificationCodeExpire extends AuthenticationException {

    public VerificationCodeExpire(String message) {
        super(message);
    }

    public VerificationCodeExpire(String message, Throwable cause) {
        super(message, cause);
    }
}
