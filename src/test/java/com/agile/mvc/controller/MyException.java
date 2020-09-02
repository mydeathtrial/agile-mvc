package com.agile.mvc.controller;

import cloud.agileframework.mvc.exception.AbstractCustomException;

/**
 * @author 佟盟
 * 日期 2020/9/00002 18:40
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class MyException extends AbstractCustomException {
    public MyException(Object... params) {
        super(params);
    }
}
