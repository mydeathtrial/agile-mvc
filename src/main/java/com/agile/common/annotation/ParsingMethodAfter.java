package com.agile.common.annotation;

import java.lang.reflect.Method;

/**
 * 描述：
 * <p>创建时间：2018/11/28<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public interface ParsingMethodAfter extends Parsing {
    void parsing(String beanName, Object object, Method method);
}
