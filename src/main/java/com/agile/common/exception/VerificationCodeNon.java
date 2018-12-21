package com.agile.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author 佟盟 on 2018/9/6
 */
public class VerificationCodeNon extends AuthenticationException {
    public VerificationCodeNon(String msg, Throwable t) {
        super(msg, t);
    }

    public VerificationCodeNon(String msg) {
        super(msg);
    }
}
