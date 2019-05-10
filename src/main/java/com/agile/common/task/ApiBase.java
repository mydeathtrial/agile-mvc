package com.agile.common.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * @author 佟盟
 * 日期 2019/5/10 14:14
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@AllArgsConstructor
@Setter
@Getter
public class ApiBase {
    private Object bean;
    private Method method;
    private String beanName;
}
