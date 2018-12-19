package com.agile.common.exception;

/**
 * Created by 佟盟 on 2018/11/6
 */
public abstract class AbstractCustomException extends Exception {
    private Object[] params;

    public Object[] getParams() {
        return params;
    }

    public AbstractCustomException(Object... params) {
        this.params = params;
    }
}
