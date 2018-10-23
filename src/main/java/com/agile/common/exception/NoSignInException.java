package com.agile.common.exception;

import org.springframework.security.authentication.AccountStatusException;

/**
 * Created by 佟盟 on 2018/7/4
 */
public class NoSignInException extends AccountStatusException {
    public NoSignInException(String msg) {
        super(msg);
    }

    public NoSignInException(String msg, Throwable t) {
        super(msg, t);
    }
}
