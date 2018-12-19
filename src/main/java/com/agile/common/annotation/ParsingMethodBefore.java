package com.agile.common.annotation;

import java.lang.reflect.Method;

/**
 * 描述：bean加载之前解析方法级的自定义注解解析器
 * <p>创建时间：2018/11/28<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public interface ParsingMethodBefore extends Parsing {
    /**
     * 解析过程
     *
     * @param beanName beanName
     * @param object   bean
     * @param method   method
     */
    void parsing(String beanName, Object object, Method method);
}
