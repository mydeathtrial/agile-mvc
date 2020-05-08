package com.agile.common.exception;

import javax.servlet.ServletException;

/**
 * @author 佟盟 on 2018/7/4
 */
public class VerificationCodeException extends ServletException {
    public VerificationCodeException(String format) {
        super(format);
    }

    public VerificationCodeException() {

    }
}
