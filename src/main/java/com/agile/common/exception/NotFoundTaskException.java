package com.agile.common.exception;

/**
 * @author 佟盟
 * 日期 2019/4/16 13:51
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class NotFoundTaskException extends Exception {
    public NotFoundTaskException() {
        super();
    }

    public NotFoundTaskException(String message) {
        super(message);
    }

    public NotFoundTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundTaskException(Throwable cause) {
        super(cause);
    }

    protected NotFoundTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
